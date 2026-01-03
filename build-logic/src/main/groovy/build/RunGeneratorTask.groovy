package build

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations

import javax.inject.Inject

abstract class RunGeneratorTask extends DefaultTask {
    @OutputDirectory
    abstract DirectoryProperty getOutputDirectory()

    @Input
    abstract ListProperty<String> getEntrypoints()

    @Classpath
    @InputFiles
    abstract ConfigurableFileCollection getGeneratorClasspath()

    @Classpath
    @InputFiles
    abstract ConfigurableFileCollection getDslClasspath()

    @Input
    abstract Property<String> getOutputPackage()

    @Inject
    protected abstract ExecOperations getExecOperations()

    @TaskAction
    void run() {
        if (outputDirectory.get().asFile.exists()) {
            outputDirectory.get().asFile.deleteDir()
        }
        execOperations.javaexec {
            it.classpath = generatorClasspath
            it.mainClass.set("dev.lukebemish.chronicle.generator.DslCreator")
            def runArgs = [
                "-o", outputDirectory.get().asFile.absolutePath,
                "--package", outputPackage.get(),
                "-cp", dslClasspath.asPath
            ]
            for (String entrypoint : entrypoints.get()) {
                runArgs.add("--entrypoint")
                runArgs.add(entrypoint)
            }
            it.args = runArgs
        }.rethrowFailure().assertNormalExitValue()
    }
}
