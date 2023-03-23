package io.margeta.jvmlabs.build.version;

import io.margeta.jvmlabs.build.version.service.TreeState;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import static io.margeta.jvmlabs.build.version.Preconditions.checkArgument;
import static io.margeta.jvmlabs.build.version.Preconditions.checkMatches;
import static io.margeta.jvmlabs.build.version.Preconditions.checkNotNull;
import static io.margeta.jvmlabs.build.version.Strings.isNotBlank;

final class Metadata {
    private static final String DIRTY_TREE_VALUE = "dirty";
    private static final String MUST_BE_GTE_ZERO = "must be greater than or equal to zero";
    private static final Pattern commitIdPattern = Pattern.compile("[0-9a-fA-F]{7,40}");
    private static final Pattern parsePattern =
            Pattern.compile("^(\\d*)\\.(" + commitIdPattern.pattern() + ")(?:\\.(" + DIRTY_TREE_VALUE + "))?$");

    private final @Nullable Long buildNumber;
    private final @Nullable String commitId;
    private final TreeState treeState;

    private Metadata(@Nullable Long buildNumber, @Nullable String commitId, TreeState treeState) {
        this.buildNumber = buildNumber;
        this.commitId = commitId;
        this.treeState = treeState;
    }

    public static Metadata make(@Nullable Long buildNumber, @Nullable String commitId, TreeState treeState) {
        checkArgument(buildNumber == null || buildNumber >= 0, MUST_BE_GTE_ZERO);
        if (commitId != null) {
            checkMatches(commitIdPattern, commitId);
        }
        return new Metadata(buildNumber, commitId != null ? commitId.toLowerCase() : null, treeState);
    }

    public static Metadata parse(String input) {
        checkNotNull(input, "input");
        final var matcher = parsePattern.matcher(input);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Illegal argument, not a valid  metadata representation.");
        }
        try {
            final var buildNumber = Long.parseLong(matcher.group(1));
            final var commitId = matcher.group(2);
            final var buildType = matcher.group(3);
            return Metadata.make(
                    buildNumber, commitId, DIRTY_TREE_VALUE.equals(buildType) ? TreeState.DIRTY : TreeState.CLEAN);
        } catch (NumberFormatException ignored) {
            throw new IllegalArgumentException("Illegal argument, not a valid  metadata representation.");
        }
    }

    @Nullable
    public Long getBuildNumber() {
        return buildNumber;
    }

    @Nullable
    public String getCommitId() {
        return commitId;
    }

    public TreeState getTreeState() {
        return treeState;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof Metadata that
                && Objects.equals(buildNumber, that.buildNumber)
                && Objects.equals(commitId, that.commitId)
                && treeState == that.treeState;
    }

    @Override
    public int hashCode() {
        int result = buildNumber != null ? buildNumber.hashCode() : 0;
        result = 31 * result + (commitId != null ? commitId.hashCode() : 0);
        result = 31 * result + treeState.ordinal();
        return result;
    }

    @Override
    public String toString() {
        final var metadata = new StringJoiner(".");
        if (buildNumber != null) {
            metadata.add(buildNumber.toString());
        }
        if (isNotBlank(commitId)) {
            metadata.add(commitId);
        }
        if (treeState == TreeState.DIRTY) {
            metadata.add(DIRTY_TREE_VALUE);
        }
        return metadata.toString();
    }
}
