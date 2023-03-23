package io.margeta.jvmlabs.build.version.service

import io.margeta.jvmlabs.build.version.JGitSpecification
import org.gradle.api.provider.Property
import spock.lang.Subject
import spock.lang.Title

import java.time.Instant

import static TreeState.CLEAN
import static TreeState.DIRTY

@Subject(GitService)
@Title("git service")
class GitServiceTest extends JGitSpecification {
    private GitService sut

    void setup() {
        def projectDirectory = Stub(Property<File>)
        def ceilingDirectory = Stub(Property<File>)
        sut = new GitService() {
            @Override
            GitService.Parameters getParameters() {
                return new GitService.Parameters() {
                    @Override
                    Property<File> getProjectDirectory() {
                        return projectDirectory
                    }

                    @Override
                    Property<File> getCeilingDirectory() {
                        return ceilingDirectory
                    }
                }
            }
        }
        projectDirectory.get() >> repository
        ceilingDirectory.getOrNull() >> repository
    }

    def "describes a project with a commit"() {
        given: "a project with a commit"
        git.init()
        new File(repository, "README.md") << "# README"
        git.add "README.md"
        git.commit "Initial commit"

        when: "describing the project"
        def actual = sut.describe().orElseThrow()

        then: "describes the project"
        with(actual) {
            tag() == null
            depth() == null
            commitId() == commit.id.abbreviate(7).name()
            treeState() == CLEAN
        }
    }

    def "describes a project with a tagged commit"() {
        given: "a project with a tagged commit"
        git.init()
        new File(repository, "README.md") << "# README"
        git.add "README.md"
        git.commit "Initial commit"
        git.tag "0.1.0"
        new File(repository, "CHANGELOG.md") << "Initial release"
        git.commit "Add changelog"

        when: "describing the project"
        def actual = sut.describe().orElseThrow()

        then: "describes the project"
        with(actual) {
            tag() == "0.1.0"
            depth() == 1
            commitId() == commit.id.abbreviate(7).name()
            treeState() == CLEAN
        }
    }

    def "describes a project with a commit tagged in a specific pattern"() {
        given: "a project with a tagged commit"
        git.init()
        new File(repository, "README.md") << "# README"
        git.add "README.md"
        git.commit "Initial commit"
        git.tag "service-a-1.0.0"

        and: "a commit tagged with the required pattern"
        new File(repository, "CHANGELOG.md") << "Initial release"
        git.commit "Add changelog"
        git.tag("service-b-1.1.0")

        when: "describing the project"
        def actual = sut.describe("service-a-*").orElseThrow()

        then: "describes the project"
        with(actual) {
            tag() == "service-a-1.0.0"
            depth() == 1
            commitId() == commit.id.abbreviate(7).name()
            treeState() == CLEAN
        }
    }

    def "describes a project with a commit and uncommitted changes"() {
        given: "a project with a commit"
        git.init()
        new File(repository, "README.md") << "# README"
        git.add "README.md"
        git.commit "Initial commit"
        git.tag "0.1.0"

        and: "uncommitted changes"
        new File(repository, "CHANGELOG.md") << "Initial release"
        git.add "CHANGELOG.md"

        when: "describing the project"
        def actual = sut.describe().orElseThrow()

        then: "describes the project"
        with(actual) {
            tag() == "0.1.0"
            depth() == 0
            commitId() == commit.id.abbreviate(7).name()
            treeState() == DIRTY
        }
    }

    def "does not describe a project that is not a repository"() {
        given: "a project that is not a repository"

        when: "describing the project"
        def actual = sut.describe()

        then: "does not describe the project"
        actual.empty
    }

    def "does not describe a project without commits"() {
        given: "a project without commits"
        git.init()

        when: "describing the project"
        def actual = sut.describe()

        then: "does not describe the project"
        actual.empty
    }

    def "finds a project head commit"() {
        given: "a project with a commit"
        git.init()
        new File(repository, "README.md") << "# README"
        git.add "README.md"
        git.commit "Initial commit"

        when: "finding the project head commit"
        def actual = sut.head().orElseThrow()

        then: "finds the project head commit"
        with(actual) {
            id() == commit.name()
            time() == Instant.ofEpochSecond(commit.commitTime)
        }
    }

    def "does not find the head commit of a project that is not a repository"() {
        given: "a project that is not a repository"

        when: "finding the project head commit"
        def actual = sut.head()

        then: "does not find the head commit"
        actual.empty
    }

    def "does not find the head commit of a project without commits"() {
        given: "a project without commits"
        git.init()

        when: "finding the project head commit"
        def actual = sut.head()

        then: "does not find the head commit"
        actual.empty
    }

    def "calculates a project commit depth"() {
        given: "a project with a commit"
        git.init()
        new File(repository, "README.md") << "# README"
        git.add "README.md"
        git.commit "Initial commit"

        when: "calculating the project commit depth"
        def actual = sut.commitDepth().orElseThrow()

        then: "calculates the project commit depth"
        actual == 1
    }

    def "does not calculate a project commit depth for a project that is not a repository"() {
        given: "a project that is not a repository"

        when: "calculating the project commit depth"
        def actual = sut.commitDepth()

        then: "does not calculate the project depth"
        actual.empty
    }

    def "does not calculate a project commit depth for a project without commits"() {
        given: "a project without commits"
        git.init()

        when: "calculating the project commit depth"
        def actual = sut.commitDepth()

        then: "calculates the project commit depth"
        actual.empty
    }
}
