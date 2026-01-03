package dev.lukebemish.chronicle.gradle;

import dev.lukebemish.chronicle.core.ChronicleEngine;
import dev.lukebemish.chronicle.gradle.dsl.impl.ContextPlugin;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public abstract class WriteChronicleTask extends DefaultTask {
    @Nested
    public abstract ListProperty<GenerationTask<?>> getGenerationTasks();
    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();
    @Input
    public abstract Property<Boolean> getPrettyPrint();

    @InputFiles
    @Classpath
    public abstract ConfigurableFileCollection getClassScanPaths();

    @Input
    public abstract Property<Boolean> getScanClasses();

    @TaskAction
    public void run() throws IOException {
        var outputDirectory = getOutputDirectory().get().getAsFile().toPath();
        boolean prettyPrint = getPrettyPrint().get();
        if (!Files.exists(outputDirectory)) {
            Files.createDirectories(outputDirectory);
        } else {
            try (var stream = Files.walk(outputDirectory)
                .filter(path -> !path.equals(outputDirectory))
                .sorted(Comparator.reverseOrder())) {
                stream.forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            }
        }
        for (var task : getGenerationTasks().get()) {
            var result = executeTask(task);
            var string = task.serializer().serialize(result, prettyPrint);
            var outputPath = outputDirectory.resolve(task.relativePath());
            var parentDir = outputPath.getParent();
            if (!Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }
            Files.writeString(outputPath, string);
        }
    }

    private <T> Object executeTask(GenerationTask<T> task) {
        var engine = new ChronicleEngine<>(task.clazz(), builder -> {
            builder.add(ContextPlugin.CLASS_SCANNER, getScanClasses().get() ? Optional.of(new ContextPlugin.ClassScanner() {
                @Override
                public List<String> findInPackage(String packageName) {
                    var mixins = new ArrayList<String>();
                    for (var root : getClassScanPaths()) {
                        var within = root.toPath().resolve(packageName.replace('.', '/'));
                        if (Files.exists(within)) {
                            try (var stream = Files.walk(within)) {
                                stream.filter(path -> path.toString().endsWith(".class"))
                                    .forEach(clazz -> {
                                        try (var classStream = Files.newInputStream(clazz)) {
                                            var reader = new ClassReader(classStream);
                                            var node = new ClassNode();
                                            reader.accept(node, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                                            var annotations = new ArrayList<AnnotationNode>();
                                            if (node.visibleAnnotations != null) {
                                                annotations.addAll(node.visibleAnnotations);
                                            }
                                            if (node.invisibleAnnotations != null) {
                                                annotations.addAll(node.invisibleAnnotations);
                                            }
                                            boolean isMixin = false;
                                            for (var annotation : annotations) {
                                                if (annotation.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")) {
                                                    isMixin = true;
                                                    break;
                                                }
                                            }
                                            if (isMixin) {
                                                var relative = within.relativize(clazz).toString();
                                                var className = relative.substring(0, relative.length() - 6).replace('/', '.').replace('\\', '.');
                                                mixins.add(className);
                                            }
                                        } catch (IOException e) {
                                            throw new UncheckedIOException(e);
                                        }
                                    });
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        }
                    }
                    return mixins;
                }
            }) : Optional.empty());
        });
        return engine.execute(task.action());
    }
}
