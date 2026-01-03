package dev.lukebemish.chronicle.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.tasks.SourceSetContainer;

public class ChroniclePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(JavaBasePlugin.class);
        project.getExtensions().create("chronicle", ChronicleExtension.class);
        project.getExtensions().configure(SourceSetContainer.class, sourceSets -> sourceSets.configureEach(sourceSet -> {
            sourceSet.getExtensions().add("chronicle", project.getObjects().newInstance(ChronicleSourceSetExtension.class, sourceSet));
        }));
    }
}
