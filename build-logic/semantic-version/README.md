# Semantic Version—Add SemVer to your Gradle

![stable](https://img.shields.io/badge/stability-stable-brightgreen.svg)
![build](https://img.shields.io/github/actions/workflow/status/zmargeta/jvm-labs/build?logo=github)
![language](https://img.shields.io/github/languages/top/zmargeta/jvm-labs)

Semantic Version is a [Gradle](https://gradle.org/) plugin that allows you to set your project version according to
the [semantic versioning](https://semver.org) scheme.
Semantic Version plugin relaxes the rules for normal version number to allow the use
of [calendar versioning](https://calver.org) scheme as well.

## SemVer scheme

The semantic versioning scheme consists of a mandatory **normal version number** and optional **pre-release version**
and **build metadata**.

### Normal version number

A normal version number must take the form `X.Y.Z`, where `X`, `Y` and `Z` are non-negative integers and **can** contain
leading zeros.
This rule is relaxed compared to the semantic versioning scheme, where leading zeros are not allowed.
`X` is the major, `Y` the minor and `Z` the patch version.
Each version number must increase numerically.

Patch version must be incremented if only backwards compatible bug fixes are introduced.
Minor version must be incremented if new, backwards compatible functionality is introduced to the public API.
And the major version must be incremented if any backwards incompatible changes are introduced to the public API.

### Pre-release version

Pre-release version may be denoted by appending the hyphen and a series of dot separated identifiers immediately
following the patch version.
Identifier must comprise only ASCII alphanumeric characters and hyphens—`[0-9a-zA-Z-]`.

### Build metadata

Build metadata may be denoted by appending a plus sign and a series of dot separated identifiers immediately following
the patch or pre-release version.
Identifiers must comprise only ASCII alphanumeric characters and hyphens—`[0-9a-zA-Z-]`.

## CalVer scheme

Calendar versioning scheme allows, in addition to semantic versions, specifying the major, minor and patch versions
using the following formats:

- `YYYY`—full year—2006, 2016, 2106
- `YY`—short year—6, 16, 106
- `0Y`—zero-padded year—06, 16, 106
- `MM`—short month—1–12
- `0M`—zero-padded month—01–12
- `WW`—short week—1–52
- `0W`—zero-padded week—01–53
- `DD`—short day—1–31
- `0D`—zero-padded day—01–31

Date segments are one-based, and short and zero-padded years are relative to the year 2000.

## Usage

You can set your Gradle project version to a semantic version by applying the plugin.

**settings.gradle.kts**

```kotlin
pluginManagement {
    includeBuild("build-logic")
}
```

**build.gradle.kts**

```kotlin
plugins {
    id("io.margeta.jvmlabs.build.semantic-version")
}
```

Project's version is automatically set to the configured semantic version, there is no need to run a task or set it
manually in the build script.
The plugin automatically sets the version of the project and all its subprojects.

The plugin adds the following objects to the project:

- A `semanticVersion` extension used to configure the plugin.
- A `versionInfo` task used to output the version information.

To access the project's [SemanticVersion](src/main/java/io/margeta/jvmlabs/build/version/SemanticVersion.java) you can use
the `semanticVersion` extension.

**build.gradle.kts**

```kotlin
import io.margeta.jvmlabs.build.version.SemanticVersionExtension

val semanticVersion = rootProject.the<SemanticVersionExtension>()
```

## Output version information

You can output the project version and include it as a project resource by running the `versionInfo` task:

```shell
gradle versionInfo
```

The `versionInfo` task creates the `build/resources/main/version.toml` file containing the project version.
Depending on whether the project has commits or not, and build info is to be included, this file may contain additional
build information.

This is the content of a typical `version.toml` file for a project that has at least one commit:

```toml
version = "0.1.0-SNAPSHOT+0.cb0a255"
branch = "main"
commit_id = "cb0a2551d2f787596e0aaee5becbbd92c908c723"
commit_id_abbrev = "cb0a255"
commit_message = "--wip-- [skip ci]-"
commit_time = 2023-05-02T22:21:35Z
commit_user_email = "zeljko.margeta@gmail.com"
commit_user_name = "Željko Margeta"
build_number = 0
dirty = false
host = "MacBook-Pro.local"
```

The task is not configured as a dependency of any other task, so if you require this behavior you must configure it
in `build.gradle.kts`:

```kotlin
configure(allprojects) {
    tasks.withType<ProcessResources>().configureEach {
        dependsOn(tasks.withType<VersionInfo>())
    }
}
```

### Alternate file names and formats

Apart for the default `version.toml` file, version information file name can be customized as well as the format it is
outputted in.
Supported file formatters are `TOML`, `YAML`, `HOCON` and `JSON`.

Here are some examples to illustrate configuring custom file names or formats of version information files.

### Declare a custom version information file name

**build.gradle.kts**

```kotlin
semanticVersion {
    versionInfo {
        fileName = "build_info.toml"
    }
}
```

Version information will be outputted to the `build/resources/main/build_info.toml` file, keeping in mind that
the `.toml` file extension does not dictate the format in which it is outputted.

### Declare a YAML format of the version information file

**build.gradle.kts**

```kotlin
semanticVersion {
    versionInfo {
        fileName = "version.yaml"
        formatter = "YAML"
    }
}
```

The resulting `build/resources/main/version.yaml` file would contain the following content.

```yaml
version: 0.1.0-SNAPSHOT+0.cb0a255
branch: main
commitId: cb0a2551d2f787596e0aaee5becbbd92c908c723
commitIdAbbrev: cb0a255
commitMessage: '--wip-- [skip ci]-'
commitTime: '2023-05-02T22:21:35Z'
commitUserEmail: zeljko.margeta@gmail.com
commitUserName: Željko Margeta
buildNumber: 0
dirty: false
host: MacBook-Pro.local
```

### Declare a HOCON format of the version information file

**build.gradle.kts**

```kotlin
semanticVersion {
    versionInfo {
        fileName = "version.conf"
        formatter = "HOCON"
    }
}
```

The resulting `build/resources/main/version.conf` file would contain the following content.

```hocon
version: "0.1.0-SNAPSHOT+0.cb0a255"
branch: main
commit-id: cb0a2551d2f787596e0aaee5becbbd92c908c723
commit-id-abbrev: cb0a255
commit-message: "--wip-- [skip-ci]-"
commit-time: "2023-05-02T22:21:35Z"
commit-user-email: "zeljko.margeta@gmail.com"
commit-user-name: Željko Margeta
build-number: 0
dirty: false
host: MacBook-Pro.local
```

### Declare a JSON format of the version information file

**build.gradle.kts**

```kotlin
semanticVersion {
    versionInfo {
        fileName = "version.json"
        formatter = "JSON"
    }
}
```

The resulting `build/resources/main/version.json` file would contain the following content.

```json
{
    "version": "0.1.0-SNAPSHOT+0.cb0a255",
    "branch": "main",
    "commitId": "cb0a2551d2f787596e0aaee5becbbd92c908c723",
    "commitIdAbbrev": "cb0a255",
    "commitMessage": "--wip-- [skip-ci]-",
    "commitTime": "2023-05-02T22:21:35Z",
    "commitUserEmail": "zeljko.margeta@gmail.com",
    "commitUserName": "Željko Margeta",
    "buildNumber": 0,
    "dirty": false,
    "host": "MacBook-Pro.local"
}
```

## Configuration

The plugin supports using a semantic version or a calendar version as the project's version.
By convention, the project's version is set to `0.1.0-SNAPSHOT+<build_number>.<commit_id>`.

The `build_number` is the number of commits on top of the latest tag, and the `commit_id` the latest commit identifier.
If the project is not a git repository, the build metadata is not set, or there are no tags, the `build_number` is
occluded and the build metadata equals just the `commit_id`.
In case the work tree has uncommitted changes, the suffix `.dirty` is appended to the build metadata.

All the configuration options can be
initialized [lazily](https://docs.gradle.org/current/userguide/lazy_configuration.html) using a provider.

### Configuration examples

Here are some examples to illustrate the configurability of the project's version.

### Declare a semantic version

**build.gradle.kts**

```kotlin
semanticVersion {
    major = 1            // 1.
    minor = 2            // 2.
    patch = 1            // 3.
    preRelease = "alpha" // 4.
}
```

1. Overwrite the project's `major` version to `1`.
2. Overwrite the project's `minor` version to `2`.
3. Overwrite the project's `patch` version to `1`.
4. Overwrite the project's `preRelease` version to `alpha`.

### Declare a version with specific build metadata

**build.gradle.kts**

```kotlin
semanticVersion {
    metadata = "20230422.14" // 1.
}
```

1. Overwrite the project's build `metadata` to `20230422.14`.

### Declare a tag pattern to use when generating build metadata

**build.gradle.kts**

```kotlin
semanticVersion {
    tagPattern = "service-*" // 1.
}
```

1. Configure the build metadata generation to use the tag pattern `service-`.

When finding the latest tag, `git describe --tags --always --long --match 'service-*'`
is executed under the hood, with an additional check if the work tree has uncommitted changes.
The `tagPattern` must be a valid
[glob pattern](https://git-scm.com/docs/git-describe#Documentation/git-describe.txt---matchltpatterngt).

### Declare a calendar version

**build.gradle.kts**

```kotlin
semanticVersion {
    majorFormat = "YY" // 1.
    minorFormat = "MM" // 2.
    patchFormat = "DD" // 3.
}
```

1. Override the project's `major` version to the current year—using `YY` format.
2. Override the project's `minor` version to the current month—using `MM` format.
3. Override the project's `patch` version to the current day—using `DD` format.

See [CalVer scheme](#calver-scheme) for details about supported formats.

### Declare a calendar version pinned at a specific date

**build.gradle.kts**

```kotlin
import kotlinx.datetime.LocalDate

semanticVersion {
    date = LocalDate(2023, 4, 22).toJavaLocalDate() // 1.
    majorFormat = "YY"                              // 2.
    minorFormat = "MM"                              // 3.
}
```

1. Override the `date` used for calendar version numbers.
2. Override the project's `major` version to year of `date`—using `YY` format.
3. Override the project's `minor` version to month of `date`—using `MM` format.

### Declare a version using mixed versioning schemes

**build.gradle.kts**

```kotlin
semanticVersion {
    major = 1          // 1.
    minorFormat = "0M" // 2.
}
```

1. Override the project's `major` version to `1`.
2. Override the project's `minor` version to current month—using `0M` format.
