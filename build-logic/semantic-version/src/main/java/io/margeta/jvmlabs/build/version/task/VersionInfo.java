package io.margeta.jvmlabs.build.version.task;

import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.StringJoiner;

public final class VersionInfo {
    private final String version;
    private final @Nullable String branch;
    private final @Nullable String commitId;
    private final @Nullable String commitIdAbbrev;
    private final @Nullable String commitMessage;
    private final @Nullable OffsetDateTime commitTime;
    private final @Nullable String commitUserName;
    private final @Nullable String commitUserEmail;
    private final @Nullable Long buildNumber;
    private final @Nullable Boolean dirty;
    private final @Nullable String host;

    VersionInfo(VersionInfoBuilder builder) {
        version = builder.version;
        branch = builder.branch;
        commitId = builder.commitId;
        commitIdAbbrev = builder.commitIdAbbrev;
        commitMessage = builder.commitMessage;
        commitTime = builder.commitTime;
        commitUserName = builder.commitUserName;
        commitUserEmail = builder.commitUserEmail;
        buildNumber = builder.buildNumber;
        dirty = builder.dirty;
        host = builder.host;
    }

    static VersionInfoBuilder makeBuilder() {
        return new VersionInfoBuilder();
    }

    public String getVersion() {
        return version;
    }

    @Nullable
    public String getBranch() {
        return branch;
    }

    public boolean hasBranch() {
        return branch != null;
    }

    @Nullable
    public String getCommitId() {
        return commitId;
    }

    public boolean hasCommitId() {
        return commitId != null;
    }

    @Nullable
    public String getCommitIdAbbrev() {
        return commitIdAbbrev;
    }

    public boolean hasCommitIdAbbrev() {
        return commitIdAbbrev != null;
    }

    @Nullable
    public String getCommitMessage() {
        return commitMessage;
    }

    public boolean hasCommitMessage() {
        return commitMessage != null;
    }

    @Nullable
    public OffsetDateTime getCommitTime() {
        return commitTime;
    }

    public boolean hasCommitTime() {
        return commitTime != null;
    }

    @Nullable
    public String getCommitUserName() {
        return commitUserName;
    }

    public boolean hasCommitUserName() {
        return commitUserName != null;
    }

    @Nullable
    public String getCommitUserEmail() {
        return commitUserEmail;
    }

    public boolean hasCommitUserEmail() {
        return commitUserEmail != null;
    }

    @Nullable
    public Long getBuildNumber() {
        return buildNumber;
    }

    public boolean hasBuildNumber() {
        return buildNumber != null;
    }

    @Nullable
    public Boolean getDirty() {
        return dirty;
    }

    public boolean hasDirty() {
        return dirty != null;
    }

    @Nullable
    public String getHost() {
        return host;
    }

    public boolean hasHost() {
        return host != null;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return other instanceof VersionInfo that
                && version.equals(that.version)
                && Objects.equals(branch, that.branch)
                && Objects.equals(commitId, that.commitId)
                && Objects.equals(commitIdAbbrev, that.commitIdAbbrev)
                && Objects.equals(commitMessage, that.commitMessage)
                && Objects.equals(commitTime, that.commitTime)
                && Objects.equals(commitUserName, that.commitUserName)
                && Objects.equals(commitUserEmail, that.commitUserEmail)
                && Objects.equals(buildNumber, that.buildNumber)
                && Objects.equals(dirty, that.dirty)
                && Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        int result = version.hashCode();
        result = 31 * result + (branch != null ? branch.hashCode() : 0);
        result = 31 * result + (commitId != null ? commitId.hashCode() : 0);
        result = 31 * result + (commitIdAbbrev != null ? commitIdAbbrev.hashCode() : 0);
        result = 31 * result + (commitMessage != null ? commitMessage.hashCode() : 0);
        result = 31 * result + (commitTime != null ? commitTime.hashCode() : 0);
        result = 31 * result + (commitUserName != null ? commitUserName.hashCode() : 0);
        result = 31 * result + (commitUserEmail != null ? commitUserEmail.hashCode() : 0);
        result = 31 * result + (buildNumber != null ? buildNumber.hashCode() : 0);
        result = 31 * result + (dirty != null ? dirty.hashCode() : 0);
        result = 31 * result + (host != null ? host.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", VersionInfo.class.getSimpleName() + "(", ")")
                .add("version='" + version + "'")
                .add("branch='" + branch + "'")
                .add("commitId='" + commitId + "'")
                .add("commitIdAbbrev='" + commitIdAbbrev + "'")
                .add("commitMessage='" + commitMessage + "'")
                .add("commitTime=" + commitTime)
                .add("commitUserName='" + commitUserName + "'")
                .add("commitUserEmail='" + commitUserEmail + "'")
                .add("buildNumber=" + buildNumber)
                .add("dirty=" + dirty)
                .add("host='" + host + "'")
                .toString();
    }
}
