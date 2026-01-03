package dev.lukebemish.chronicle.gradle;

import dev.lukebemish.chronicle.core.Action;
import dev.lukebemish.chronicle.plugin.dsl.generated.dev.lukebemish.chronicle.fabric.FabricModJsonImpl;
import dev.lukebemish.chronicle.plugin.dsl.generated.dev.lukebemish.chronicle.mixin.MixinConfigImpl;
import dev.lukebemish.chronicle.plugin.dsl.generated.dev.lukebemish.chronicle.neoforge.NeoForgeModsTomlImpl;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.language.jvm.tasks.ProcessResources;

import javax.inject.Inject;

public abstract class ChronicleSourceSetExtension {
    private final TaskProvider<WriteChronicleTask> chronicleTask;

    @Inject
    protected abstract Project getProject();

    public abstract Property<Boolean> getPrettyPrint();

    @Inject
    public ChronicleSourceSetExtension(SourceSet sourceSet) {
        getPrettyPrint().convention(true);
        var chronicleDir = getProject().getLayout().getBuildDirectory().map(it -> it.dir("chronicle/"+sourceSet.getName()));
        sourceSet.getResources().srcDir(chronicleDir);
        this.chronicleTask = getProject().getTasks().register(
            sourceSet.getTaskName("chronicleGenerate", null),
            WriteChronicleTask.class,
            t -> {
                t.getOutputDirectory().set(chronicleDir);
                t.getPrettyPrint().set(getPrettyPrint());
            }
        );
        getProject().getTasks().named(sourceSet.getProcessResourcesTaskName(), ProcessResources.class, t -> {
            t.dependsOn(this.chronicleTask);
        });
    }

    public void fabric(Action<FabricModJsonImpl> action) {
        this.chronicleTask.configure(t -> {
            t.getGenerationTasks().add(new GenerationTask<>(
                "fabric.mod.json",
                action,
                FabricModJsonImpl.class,
                new JsonSerializer()
            ));
        });
    }

    public void neoForge(Action<NeoForgeModsTomlImpl> action) {
        this.chronicleTask.configure(t -> {
            t.getGenerationTasks().add(new GenerationTask<>(
                "META-INF/neoforge.mods.toml",
                action,
                NeoForgeModsTomlImpl.class,
                new TomlSerializer()
            ));
        });
    }

    public void mixins(String name, Action<MixinConfigImpl> action) {
        this.chronicleTask.configure(t -> {
            t.getGenerationTasks().add(new GenerationTask<>(
                name,
                action,
                MixinConfigImpl.class,
                new JsonSerializer()
            ));
        });
    }

    public <T> void json(String name, Class<T> type, Action<T> action) {
        this.chronicleTask.configure(t -> {
            t.getGenerationTasks().add(new GenerationTask<>(
                name,
                action,
                type,
                new JsonSerializer()
            ));
        });
    }

    public <T> void toml(String name, Class<T> type, Action<T> action) {
        this.chronicleTask.configure(t -> {
            t.getGenerationTasks().add(new GenerationTask<>(
                name,
                action,
                type,
                new TomlSerializer()
            ));
        });
    }
}
