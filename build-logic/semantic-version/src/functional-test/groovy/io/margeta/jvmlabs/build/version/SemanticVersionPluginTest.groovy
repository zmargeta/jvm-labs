package io.margeta.jvmlabs.build.version

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.core.file.FileConfig
import groovy.transform.Memoized
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Subject
import spock.lang.TempDir
import spock.lang.Title

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Subject(SemanticVersionPlugin)
@Title("semantic-version plugin")
class SemanticVersionPluginTest extends JGitSpecification {
    File buildFile

    @TempDir
    File outputDir

    void setup() {
        buildFile = new File(repository, "build.gradle.kts")
        buildFile << """
            |plugins {
            |    id("jvmlabs.build.semantic-version")
            |}
            |
            |tasks.register("saveProjectProperties") {
            |    val configVersion = project.version.toString()
            |    doLast {
            |        file("${outputDir.absolutePath}/project.properties")
            |            .writeText(\"\"\"
            |                configVersion=\$configVersion
            |                actualVersion=\${project.version}
            |            \"\"\".trimIndent()
            |            )
            |    }
            |}
        """.stripMargin()
    }

    def "sets the default project version"() {
        given: "a default configuration"

        when: "configuring the project"
        def actual = GradleRunner
            .create()
            .withProjectDir(repository)
            .withArguments(":saveProjectProperties")
            .withPluginClasspath()
            .build()

        then: "sets the default project version"
        actual.task(":saveProjectProperties").outcome == SUCCESS
        with(projectProperties()) {
            getProperty("configVersion") == "0.1.0-SNAPSHOT+dirty"
            getProperty("actualVersion") == "0.1.0-SNAPSHOT+dirty"
        }
    }

    @Memoized
    private Properties projectProperties() {
        def propertiesFile = outputDir.toPath().resolve("project.properties").toFile()
        new FileInputStream(propertiesFile).withCloseable {
            def properties = new Properties()
            properties.load(it)
            properties
        }
    }

    def "sets the configured project semantic version"() {
        given: "a semantic version configuration"
        buildFile << """
            |semanticVersion {
            |    major.set(1)
            |    minor.set(2)
            |    patch.set(3)
            |    preRelease.set("alpha01")
            |}
        """.stripMargin()

        when: "configuring the project"
        def actual = GradleRunner
            .create()
            .withProjectDir(repository)
            .withArguments(":saveProjectProperties")
            .withPluginClasspath()
            .build()

        then: "sets the configured project semantic version"
        actual.task(":saveProjectProperties").outcome == SUCCESS
        with(projectProperties()) {
            getProperty("configVersion") == "1.2.3-alpha01+dirty"
            getProperty("actualVersion") == "1.2.3-alpha01+dirty"
        }
    }

    def "sets the configured project calendar version"() {
        given: "a semantic version configuration"
        buildFile << """
            |semanticVersion {
            |    date.set(java.time.LocalDate.of(2023, 5, 1))
            |    majorFormat.set("YY")
            |    minorFormat.set("MM")
            |    patchFormat.set("DD")
            |    preRelease.set("alpha01")
            |}
        """.stripMargin()

        when: "configuring the project"
        def actual = GradleRunner
            .create()
            .withProjectDir(repository)
            .withArguments(":saveProjectProperties")
            .withPluginClasspath()
            .build()

        then: "sets the configured project calendar version"
        actual.task(":saveProjectProperties").outcome == SUCCESS
        with(projectProperties()) {
            getProperty("configVersion") == "23.5.1-alpha01+dirty"
            getProperty("actualVersion") == "23.5.1-alpha01+dirty"
        }
    }

    def "sets the version of a project with a commit"() {
        given: "a project with a commit"
        git.init()
        new File(repository, "README.md") << "# README"
        git.add "README.md"
        git.commit "Initial commit"

        when: "configuring the project"
        def actual = GradleRunner
            .create()
            .withProjectDir(repository)
            .withArguments(":saveProjectProperties")
            .withPluginClasspath()
            .build()

        then: "sets the project version"
        actual.task(":saveProjectProperties").outcome == SUCCESS
        with(projectProperties()) {
            getProperty("configVersion") == "0.1.0-SNAPSHOT+0.${this.commit.abbreviate(7).name()}"
            getProperty("actualVersion") == "0.1.0-SNAPSHOT+0.${this.commit.abbreviate(7).name()}"
        }
    }

    def "sets the version of a project with a tagged commit"() {
        given: "a project with a tagged commit"
        git.init()
        new File(repository, "README.md") << "# README"
        git.add "README.md"
        git.commit "Initial commit"
        git.tag "0.1.0"
        new File(repository, "CHANGELOG.md") << "Initial release"
        git.commit "Add changelog"

        when: "configuring the project"
        def actual = GradleRunner
            .create()
            .withProjectDir(repository)
            .withArguments(":saveProjectProperties")
            .withPluginClasspath()
            .build()

        then: "sets the project version"
        actual.task(":saveProjectProperties").outcome == SUCCESS
        with(projectProperties()) {
            getProperty("configVersion") == "0.1.0-SNAPSHOT+1.${this.commit.abbreviate(7).name()}"
            getProperty("actualVersion") == "0.1.0-SNAPSHOT+1.${this.commit.abbreviate(7).name()}"
        }
    }

    def "creates version info output file of a project that is not a repository"() {
        given: "a project that is not a repository"

        when: "executing the versionInfo task"
        def actual = GradleRunner
            .create()
            .withProjectDir(repository)
            .withArguments(":versionInfo")
            .withPluginClasspath()
            .build()

        then: "creates the version info output file"
        actual.task(":versionInfo").outcome == SUCCESS
        with(versionInfo()) {
            get("version") == "0.1.0-SNAPSHOT+dirty"
            get("branch") == null
            get("commit_id") == null
            get("commit_id_abbrev") == null
            get("commit_message") == null
            get("commit_time") == null
            get("commit_user_name") == null
            get("commit_user_email") == null
            get("build_number") == null
            get("dirty") == null
            get("host") == null
        }
    }

    def "creates version info output file of a project without commits"() {
        given: "a project without commits"
        git.init()

        when: "executing the versionInfo task"
        def actual = GradleRunner
            .create()
            .withProjectDir(repository)
            .withArguments(":versionInfo")
            .withPluginClasspath()
            .build()

        then: "creates the version info output file"
        actual.task(":versionInfo").outcome == SUCCESS
        with(versionInfo()) {
            get("version") == "0.1.0-SNAPSHOT+dirty"
            get("branch") == null
            get("commit_id") == null
            get("commit_id_abbrev") == null
            get("commit_message") == null
            get("commit_time") == null
            get("commit_user_name") == null
            get("commit_user_email") == null
            get("build_number") == null
            get("dirty") == null
            get("host") == null
        }
    }

    def "creates version info toml output file of a project with a commit"() {
        given: "a project with a commit"
        git.init()
        new File(repository, "README.md") << "# README"
        git.add "README.md"
        git.commit "Initial commit", "First Last", "first_last@domain.com"

        when: "executing the versionInfo task"
        def actual = GradleRunner
            .create()
            .withProjectDir(repository)
            .withArguments(":versionInfo")
            .withPluginClasspath()
            .build()

        then: "creates the version info output file"
        actual.task(":versionInfo").outcome == SUCCESS
        with(versionInfo()) {
            get("version") == "0.1.0-SNAPSHOT+0.${this.commit.abbreviate(7).name()}"
            get("branch") == "main"
            get("commit_id") == this.commit.name
            get("commit_id_abbrev") == this.commit.abbreviate(7).name()
            get("commit_message") == "Initial commit"
            get("commit_time") == Instant.ofEpochSecond(this.commit.commitTime).atOffset(ZoneOffset.of("Z"))
            get("commit_user_name") == "First Last"
            get("commit_user_email") == "first_last@domain.com"
            get("build_number") == 0
            get("dirty") == false
            get("host") == InetAddress.localHost.hostName
        }
    }

    def "creates version info yaml output file of a project with a commit"() {
        given: "a version info configuration"
        buildFile << """
            |semanticVersion {
            |    versionInfo {
            |        fileName.set("version.yaml")
            |        formatter.set("YAML")
            |    }
            |}
        """.stripMargin()

        and: "a project with a commit"
        git.init()
        new File(repository, "README.md") << "# README"
        git.add "README.md"
        git.commit "Initial commit", "First Last", "first_last@domain.com"

        when: "executing the versionInfo task"
        def actual = GradleRunner
            .create()
            .withProjectDir(repository)
            .withArguments(":versionInfo")
            .withPluginClasspath()
            .build()

        then: "creates the version info output file"
        actual.task(":versionInfo").outcome == SUCCESS
        with(versionInfo("version.yaml")) {
            get("version") == "0.1.0-SNAPSHOT+0.${this.commit.abbreviate(7).name()}"
            get("branch") == "main"
            get("commitId") == this.commit.name
            get("commitIdAbbrev") == this.commit.abbreviate(7).name()
            get("commitMessage") == "Initial commit"
            get("commitTime") == DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochSecond(this.commit.commitTime))
            get("commitUserName") == "First Last"
            get("commitUserEmail") == "first_last@domain.com"
            get("buildNumber") == 0
            get("dirty") == false
            get("host") == InetAddress.localHost.hostName
        }
    }

    def "creates version info hocon output file of a project with a commit"() {
        given: "a version info configuration"
        buildFile << """
            |semanticVersion {
            |    versionInfo {
            |        fileName.set("version.conf")
            |        formatter.set("HOCON")
            |    }
            |}
        """.stripMargin()

        and: "a project with a commit"
        git.init()
        new File(repository, "README.md") << "# README"
        git.add "README.md"
        git.commit "Initial commit", "First Last", "first_last@domain.com"

        when: "executing the versionInfo task"
        def actual = GradleRunner
            .create()
            .withProjectDir(repository)
            .withArguments(":versionInfo")
            .withPluginClasspath()
            .build()

        then: "creates the version info output file"
        actual.task(":versionInfo").outcome == SUCCESS
        with(versionInfo("version.conf")) {
            get("version") == "0.1.0-SNAPSHOT+0.${this.commit.abbreviate(7).name()}"
            get("branch") == "main"
            get("commit-id") == this.commit.name
            get("commit-id-abbrev") == this.commit.abbreviate(7).name()
            get("commit-message") == "Initial commit"
            get("commit-time") == DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochSecond(this.commit.commitTime))
            get("commit-user-name") == "First Last"
            get("commit-user-email") == "first_last@domain.com"
            get("build-number") == 0
            get("dirty") == false
            get("host") == InetAddress.localHost.hostName
        }
    }

    def "creates version info json output file of a project with a commit"() {
        given: "a version info configuration"
        buildFile << """
            |semanticVersion {
            |    versionInfo {
            |        fileName.set("version.json")
            |        formatter.set("JSON")
            |    }
            |}
        """.stripMargin()

        and: "a project with a commit"
        git.init()
        new File(repository, "README.md") << "# README"
        git.add "README.md"
        git.commit "Initial commit", "First Last", "first_last@domain.com"

        when: "executing the versionInfo task"
        def actual = GradleRunner
            .create()
            .withProjectDir(repository)
            .withArguments(":versionInfo")
            .withPluginClasspath()
            .build()

        then: "creates the version info output file"
        actual.task(":versionInfo").outcome == SUCCESS
        with(versionInfo("version.json")) {
            get("version") == "0.1.0-SNAPSHOT+0.${this.commit.abbreviate(7).name()}"
            get("branch") == "main"
            get("commitId") == this.commit.name
            get("commitIdAbbrev") == this.commit.abbreviate(7).name()
            get("commitMessage") == "Initial commit"
            get("commitTime") == DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochSecond(this.commit.commitTime))
            get("commitUserName") == "First Last"
            get("commitUserEmail") == "first_last@domain.com"
            get("buildNumber") == 0
            get("dirty") == false
            get("host") == InetAddress.localHost.hostName
        }
    }

    @Memoized
    private Config versionInfo(String fileName = "version.toml") {
        def versionInfo = FileConfig.of(repository.toPath().resolve("build/resources/main/$fileName").toFile())
        versionInfo.load()
        versionInfo
    }
}
