plugins {
    groovy
    id("jvmlabs.build.common-conventions")
}

val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
val testLibs = project.extensions.getByType<VersionCatalogsExtension>().named("testLibs")

val commonArgs =
    listOf(
        "-Xlint:-options",
        "-Xlint:cast",
        "-Xlint:classfile",
        "-Xlint:dep-ann",
        "-Xlint:deprecation",
        "-Xlint:divzero",
        "-Xlint:empty",
        "-Xlint:fallthrough",
        "-Xlint:finally",
        "-Xlint:overrides",
        "-Xlint:path",
        "-Xlint:processing",
        "-Xlint:rawtypes",
        "-Xlint:serial",
        "-Xlint:static",
        "-Xlint:try",
        "-Xlint:unchecked",
        "-Xlint:varargs",
        "-parameters",
    )

tasks.compileJava {
    options.release = 19
    options.compilerArgs = commonArgs + "-Werror"
    options.encoding = "UTF-8"
}

tasks.compileTestJava {
    options.release = 19
    options.compilerArgs = commonArgs
    options.encoding = "UTF-8"
}

testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project())
                    implementation(testLibs.findLibrary("groovy").orElseThrow())
                    implementation.bundle(testLibs.findBundle("spock").orElseThrow())
                }
            }
        }
    }
}
