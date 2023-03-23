package io.margeta.jvmlabs.build.version.task;

import io.margeta.jvmlabs.build.version.service.GitService;
import io.margeta.jvmlabs.build.version.service.TreeState;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.tooling.BuildException;

import java.io.FileWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZoneOffset;

public abstract class VersionInfoTask extends DefaultTask {
    @ServiceReference
    public abstract Property<GitService> getGitService();

    @Input
    public abstract Property<String> getProjectVersion();

    @Input
    public abstract Property<String> getFormatter();

    @OutputFile
    public abstract RegularFileProperty getOutput();

    @TaskAction
    public void writeOutput() {
        final var versionInfoBuilder = new VersionInfoBuilder();
        setVersion(versionInfoBuilder);
        setBuildInfo(versionInfoBuilder);
        serialize(versionInfoBuilder.build());
    }

    private void setVersion(VersionInfoBuilder versionInfoBuilder) {
        versionInfoBuilder.version(getProjectVersion().get());
    }

    private void setBuildInfo(VersionInfoBuilder versionInfoBuilder) {
        setCommitFields(versionInfoBuilder);
        setBuildFields(versionInfoBuilder);
    }

    private void setCommitFields(VersionInfoBuilder versionInfoBuilder) {
        getGitService().get().head().ifPresent(it -> {
            setBranch(versionInfoBuilder);
            versionInfoBuilder.commitId(it.id());
            versionInfoBuilder.commitIdAbbrev(it.idAbbrev());
            versionInfoBuilder.commitMessage(it.message());
            versionInfoBuilder.commitTime(it.time().atOffset(ZoneOffset.UTC));
            versionInfoBuilder.commitUserName(it.userName());
            versionInfoBuilder.commitUserEmail(it.userEmail());
        });
    }

    private void setBranch(VersionInfoBuilder versionInfoBuilder) {
        getGitService().get().branch().ifPresent(versionInfoBuilder::branch);
    }

    private void setBuildFields(VersionInfoBuilder versionInfoBuilder) {
        final var git = getGitService().get();
        git.describe().ifPresent(it -> {
            var buildNumber = it.depth();
            if (it.commitId() != null && it.depth() == null) {
                buildNumber = git.commitDepth().map(d -> d - 1L).orElse(0L);
            }
            versionInfoBuilder.buildNumber(buildNumber);
            versionInfoBuilder.dirty(it.treeState() == TreeState.DIRTY);
            setHost(versionInfoBuilder);
        });
    }

    private void setHost(VersionInfoBuilder versionInfoBuilder) {
        try {
            versionInfoBuilder.host(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            getLogger().warn("Failed to resolve host name.");
        }
    }

    private void serialize(VersionInfo versionInfo) {
        try (final var fileWriter = new FileWriter(getOutput().get().getAsFile())) {
            final var formatterName = getFormatter().get();
            fileWriter.write(VersionInfoFormatter.valueOf(formatterName).format(versionInfo));
        } catch (Exception e) {
            throw new BuildException("An error occurred while writing the version info to a file.", e);
        }
    }
}
