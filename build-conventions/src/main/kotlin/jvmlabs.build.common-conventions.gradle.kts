plugins {
    java
    `jvm-test-suite`
    `java-test-fixtures`
    id("com.diffplug.spotless")
    id("com.google.cloud.tools.jib")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(19)
    }
}

tasks.withType<JavaExec>().configureEach {
    javaLauncher = javaToolchains.launcherFor(java.toolchain)
}

spotless {
    kotlin {
        ktlint("0.45.2")
    }
    format("graphql") {
        target("src/*/*.graphqls", "src/*/*.graphql")
        prettier("2.0.4")
            .config(
                mapOf(
                    "tabWidth" to 4
                )
            )
    }
    format("yaml") {
        target("src/*/*.yaml")
        prettier("2.0.4")
    }
}

tasks.jar {
    manifest {
        val sysProps = System.getProperties()
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Automatic-Module-Name" to project.name.replace("-", "."),
            "Created-By" to "${sysProps["java.version"]} (${sysProps["java.specification.vendor"]})",
        )
    }
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
            testType = TestSuiteType.UNIT_TEST
        }
        val integrationTest by registering(JvmTestSuite::class) {
            useJUnitJupiter()
            testType = TestSuiteType.INTEGRATION_TEST
            dependencies {
                implementation(testFixtures.modify(project()))
            }
            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
        register<JvmTestSuite>("functionalTest") {
            useJUnitJupiter()
            testType = TestSuiteType.FUNCTIONAL_TEST
            dependencies {
                implementation(testFixtures.modify(project()))
            }
            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(integrationTest)
                    }
                }
            }
        }
    }
}

tasks.check {
    dependsOn(
        testing.suites.named("integrationTest"),
        testing.suites.named("functionalTest")
    )
}
