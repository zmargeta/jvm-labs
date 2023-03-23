package io.margeta.jvmlabs.build.version

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit
import spock.lang.AutoCleanup
import spock.lang.Specification
import spock.lang.TempDir

abstract class JGitSpecification extends Specification {
    @TempDir
    File repository

    @AutoCleanup
    GitCommands git

    RevCommit commit

    void setup() {
        git = new GitCommands()
    }

    class GitCommands implements AutoCloseable {
        private Git git

        void init() {
            git = Git.init().setDirectory(repository).setInitialBranch("main").call()
        }

        void add(String path) {
            git.add().addFilepattern(path).call()
        }

        void commit(String message, String name = null, String email = null) {
            def commitCommand = git.commit().setMessage(message)
            if (name && email) commitCommand.setAuthor(name, email)
            commit = commitCommand.call()
        }

        void tag(String name) {
            git.tag().setName(name).setAnnotated(true).call()
        }

        @Override
        void close() throws Exception {
            if (git != null) {
                git.repository.close()
            }
        }
    }
}
