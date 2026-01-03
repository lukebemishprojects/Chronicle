package dev.lukebemish.chronicle.gradle;

import dev.lukebemish.chronicle.core.ChronicleEngine;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Comparator;

public abstract class WriteChronicleTask extends DefaultTask {
    @Nested
    public abstract ListProperty<GenerationTask<?>> getGenerationTasks();
    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();
    @Input
    public abstract Property<Boolean> getPrettyPrint();

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
        var engine = new ChronicleEngine<>(task.clazz());
        return engine.execute(task.action());
    }
}
