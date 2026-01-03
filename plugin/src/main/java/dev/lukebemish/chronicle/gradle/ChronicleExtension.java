package dev.lukebemish.chronicle.gradle;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import javax.inject.Inject;

public abstract class ChronicleExtension {
    @Inject
    protected abstract Project getProject();

    public void forSourceSet(String name, Action<ChronicleSourceSetExtension> action) {
        var sourceSets = getProject().getExtensions().getByType(SourceSetContainer.class);
        this.forSourceSet(sourceSets.named(name), action);
    }

    public void forSourceSet(SourceSet sourceSet, Action<ChronicleSourceSetExtension> action) {
        var extension = sourceSet.getExtensions().getByType(ChronicleSourceSetExtension.class);
        action.execute(extension);
    }

    public void forSourceSet(NamedDomainObjectProvider<SourceSet> sourceSetProvider, Action<ChronicleSourceSetExtension> action) {
        sourceSetProvider.configure(sourceSet -> this.forSourceSet(sourceSet, action));
    }
}
