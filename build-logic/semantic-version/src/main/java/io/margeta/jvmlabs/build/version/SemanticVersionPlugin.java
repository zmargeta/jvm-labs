package io.margeta.jvmlabs.build.version;

import io.margeta.jvmlabs.build.version.service.GitService;
import io.margeta.jvmlabs.build.version.service.TreeState;
import io.margeta.jvmlabs.build.version.task.VersionInfoTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Transformer;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Provider;
import org.gradle.language.jvm.tasks.ProcessResources;
import org.gradle.util.GradleVersion;
import org.gradle.util.Path;

import static io.margeta.jvmlabs.build.version.Preconditions.checkNotNull;
import static io.margeta.jvmlabs.build.version.Preconditions.checkState;

@SuppressWarnings("unused")
public final class SemanticVersionPlugin implements Plugin<Project> {
    private static final GradleVersion gradleVersion = GradleVersion.current();
    private static final GradleVersion minSupportedGradleVersion = GradleVersion.version("9.0");

    private static final String UNSUPPORTED_GRADLE_VERSION =
            "detected %s but semantic-version plugin requires %s or higher"
                    .formatted(gradleVersion, minSupportedGradleVersion);

    private static final Transformer<String, GitService.ObjectName> objectNameToMetadata =
            it -> Metadata.make(it.depth(), it.commitId(), it.treeState()).toString();
    private static final GitService.ObjectName emptyObjectName =
            new GitService.ObjectName(null, null, null, TreeState.DIRTY);

    @Override
    public void apply(Project project) {
        checkState(gradleVersion.compareTo(minSupportedGradleVersion) >= 0, UNSUPPORTED_GRADLE_VERSION);
        checkNotNull(project, "project");
        project.getPluginManager().apply(SemanticVersionPlugin.class);
        final var extension = makeExtension(project);
        final var gitService = makeGitServiceProvider(project);
        final var metadata = makeMetadataProvider(project, gitService, extension);
        final var version = ExtendedVersionStringProvider.wrap(makeVersionProvider(project, extension, metadata));
        project.allprojects(it -> {
            it.setVersion(version);
            final var versionInfo = it.getTasks().register("versionInfo", VersionInfoTask.class, task -> {
                task.setGroup("Semantic Version");
                task.setDescription("Adds version information to resources source set.");
                task.usesService(gitService);
                task.getOutput().set(makeVersionInfoFileProvider(it, extension));
                task.getProjectVersion().set(version.map(SemanticVersion::toExtendedString));
                task.getFormatter().set(extension.getVersionInfo().getFormatter());
            });
            it.getTasks().withType(ProcessResources.class).configureEach(task -> task.mustRunAfter(versionInfo));
        });
    }

    private Provider<GitService> makeGitServiceProvider(Project project) {
        return project.getGradle().getSharedServices().registerIfAbsent("gitService", GitService.class, it -> {
            it.getParameters().getProjectDirectory().set(project.getProjectDir());
            it.getMaxParallelUsages().set(1);
        });
    }

    private SemanticVersionExtension makeExtension(Project project) {
        final var extension = project.getExtensions().create("semanticVersion", SemanticVersionExtension.class);
        extension.getMajor().convention(0);
        extension.getMinor().convention(1);
        extension.getPatch().convention(0);
        extension.getPreRelease().convention("SNAPSHOT");
        extension.getVersionInfo().getFileName().convention("version.toml");
        extension.getVersionInfo().getFormatter().convention("TOML");
        return extension;
    }

    private Provider<SemanticVersion> makeVersionProvider(
            Project project, SemanticVersionExtension extension, Provider<String> metadata) {
        return new SemanticVersionProviderBuilder(project)
                .major(extension::getMajor)
                .minor(extension::getMinor)
                .patch(extension::getPatch)
                .preRelease(extension::getPreRelease)
                .metadata(() -> extension.getMetadata().orElse(metadata))
                .date(extension::getDate)
                .majorFormat(extension::getMajorFormat)
                .minorFormat(extension::getMinorFormat)
                .patchFormat(extension::getPatchFormat)
                .build();
    }

    private Provider<String> makeMetadataProvider(
            Project project, Provider<GitService> gitService, SemanticVersionExtension extension) {
        return extension
                .getTagPattern()
                .orElse("*")
                .flatMap(it -> gitService.map(git -> {
                    try {
                        final var objectName = git.describe(it).orElse(emptyObjectName);
                        if (objectName.commitId() != null && objectName.depth() == null) {
                            return objectName.withDepth(
                                    git.commitDepth().map(d -> d - 1L).orElse(0L));
                        }
                        return objectName;
                    } catch (Exception ignored) {
                        return emptyObjectName;
                    }
                }))
                .map(objectNameToMetadata);
    }

    private Provider<RegularFile> makeVersionInfoFileProvider(Project project, SemanticVersionExtension extension) {
        return extension.getVersionInfo().getFileName().flatMap(it -> {
            final var fileName = Path.path(it).getName();
            return project.getLayout().getBuildDirectory().file("resources/main/%s".formatted(fileName));
        });
    }
}
