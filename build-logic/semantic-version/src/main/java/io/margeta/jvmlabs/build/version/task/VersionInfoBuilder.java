package io.margeta.jvmlabs.build.version.task;

import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.StringJoiner;

import static io.margeta.jvmlabs.build.version.Preconditions.checkNotNull;

final class VersionInfoBuilder {
    String version;

    @Nullable
    String branch;

    @Nullable
    String commitId;

    @Nullable
    String commitIdAbbrev;

    @Nullable
    String commitMessage;

    @Nullable
    OffsetDateTime commitTime;

    @Nullable
    String commitUserName;

    @Nullable
    String commitUserEmail;

    @Nullable
    Long buildNumber;

    @Nullable
    Boolean dirty;

    @Nullable
    String host;

    public VersionInfoBuilder version(String version) {
        checkNotNull(version, "version");
        this.version = version;
        return this;
    }

    public VersionInfoBuilder branch(@Nullable String branch) {
        this.branch = branch;
        return this;
    }

    public VersionInfoBuilder commitId(@Nullable String commitId) {
        this.commitId = commitId;
        return this;
    }

    public VersionInfoBuilder commitIdAbbrev(@Nullable String commitIdAbbrev) {
        this.commitIdAbbrev = commitIdAbbrev;
        return this;
    }

    public VersionInfoBuilder commitMessage(@Nullable String commitMessage) {
        this.commitMessage = commitMessage;
        return this;
    }

    public VersionInfoBuilder commitTime(@Nullable OffsetDateTime commitTime) {
        this.commitTime = commitTime;
        return this;
    }

    public VersionInfoBuilder commitUserName(@Nullable String commitUserName) {
        this.commitUserName = commitUserName;
        return this;
    }

    public VersionInfoBuilder commitUserEmail(@Nullable String commitUserEmail) {
        this.commitUserEmail = commitUserEmail;
        return this;
    }

    public VersionInfoBuilder buildNumber(@Nullable Long buildNumber) {
        this.buildNumber = buildNumber;
        return this;
    }

    public VersionInfoBuilder dirty(@Nullable Boolean dirty) {
        this.dirty = dirty;
        return this;
    }

    public VersionInfoBuilder host(@Nullable String host) {
        this.host = host;
        return this;
    }

    public VersionInfo build() {
        checkNotNull(version, "version");
        return new VersionInfo(this);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return other instanceof VersionInfoBuilder that
                && Objects.equals(version, that.version)
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
        int result = version != null ? version.hashCode() : 0;
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
        return new StringJoiner(", ", VersionInfoBuilder.class.getSimpleName() + "(", ")")
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
