package io.margeta.jvmlabs.build.version.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.gradle.api.provider.Property;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.gradle.tooling.BuildException;

import javax.annotation.Nullable;
import java.io.File;
import java.time.Instant;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import static io.margeta.jvmlabs.build.version.Preconditions.checkNotNull;

public abstract class GitService implements BuildService<GitService.Parameters>, AutoCloseable {
    private Git git;

    private Git getGit() {
        if (git == null) {
            git = Git.wrap(findRepository(
                    getParameters().getProjectDirectory().get(),
                    getParameters().getCeilingDirectory().getOrNull()));
        }
        return git;
    }

    private Repository findRepository(File projectDirectory, @Nullable File ceilingDirectory) {
        try {
            return new FileRepositoryBuilder()
                    .readEnvironment()
                    .findGitDir(projectDirectory)
                    .addCeilingDirectory(ceilingDirectory)
                    .build();
        } catch (Exception e) {
            throw new BuildException("An error occurred while finding the git repository.", e);
        }
    }

    public Optional<ObjectName> describe() {
        return describe("*");
    }

    public Optional<ObjectName> describe(String pattern) {
        if (pattern.isBlank()) {
            pattern = "*";
        }
        try {
            final var objectName = new StringJoiner("-");
            final var desc = getGit().describe()
                    .setTags(true)
                    .setAlways(true)
                    .setLong(true)
                    .setMatch(pattern)
                    .call();
            objectName.add(desc);
            if (getGit().status().call().hasUncommittedChanges()) {
                objectName.add(ObjectName.DIRTY_TREE_VALUE);
            }
            return Optional.of(ObjectName.parse(objectName.toString()));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    public Optional<Commit> head() {
        try (final var objectReader = getGit().getRepository().newObjectReader()) {
            final var revWalk = new RevWalk(getGit().getRepository());
            final var head = revWalk.parseCommit(getGit().getRepository().resolve(Constants.HEAD));
            return Optional.of(new Commit(
                    head.name(),
                    objectReader.abbreviate(head).name(),
                    head.getShortMessage(),
                    Instant.ofEpochSecond(head.getCommitTime()),
                    head.getAuthorIdent().getEmailAddress(),
                    head.getAuthorIdent().getName()));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    public Optional<String> branch() {
        try {
            return Optional.of(getGit().getRepository().getBranch());
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    public Optional<Long> commitDepth() {
        try {
            final var commits = getGit().log().call();
            final var commitDepth =
                    StreamSupport.stream(commits.spliterator(), false).count();
            return Optional.of(commitDepth);
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    @Override
    public void close() {
        if (git != null) {
            git.getRepository().close();
        }
    }

    public interface Parameters extends BuildServiceParameters {
        Property<File> getProjectDirectory();

        Property<File> getCeilingDirectory();
    }

    public record ObjectName(
            @Nullable String tag, @Nullable Long depth, @Nullable String commitId, TreeState treeState) {
        private static final String DIRTY_TREE_VALUE = "dirty";

        private static final Pattern parsePattern =
                Pattern.compile("(?:([0-9a-f]{7,40})|(.*)-(\\d)-g([0-9a-f]{7,40}))(?:-(" + DIRTY_TREE_VALUE + "))?");

        public static ObjectName parse(String input) {
            checkNotNull(input, "input");
            final var matcher = parsePattern.matcher(input);
            if (!matcher.matches()) {
                throw new IllegalArgumentException(
                        "Illegal argument, not a valid git repository object name representation.");
            }
            if (matcher.group(1) != null) {
                final var treeState = DIRTY_TREE_VALUE.equals(matcher.group(5)) ? TreeState.DIRTY : TreeState.CLEAN;
                return new ObjectName(null, null, matcher.group(1), treeState);
            }
            try {
                final var depth = Long.parseLong(matcher.group(3));
                final var treeState = DIRTY_TREE_VALUE.equals(matcher.group(5)) ? TreeState.DIRTY : TreeState.CLEAN;
                return new ObjectName(matcher.group(2), depth, matcher.group(4), treeState);
            } catch (NumberFormatException ignored) {
                throw new IllegalArgumentException(
                        "Illegal argument, not a valid git repository object name representation.");
            }
        }

        public ObjectName withDepth(Long value) {
            return new ObjectName(tag, value, commitId, treeState);
        }
    }

    public record Commit(String id, String idAbbrev, String message, Instant time, String userEmail, String userName) {}
}
