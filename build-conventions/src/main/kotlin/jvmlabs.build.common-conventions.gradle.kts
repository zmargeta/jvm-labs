plugins {
    java
    `jvm-test-suite`
    `java-test-fixtures`
    id("com.diffplug.spotless")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
}

tasks.withType<JavaExec>().configureEach {
    javaLauncher = javaToolchains.launcherFor(java.toolchain)
}

spotless {
    kotlin {
        ktlint("1.7.1")
    }
    format("yaml") {
        target("src/*/*.yaml")
        prettier("3.6.2")
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
        }
        val integrationTest by registering(JvmTestSuite::class) {
            useJUnitJupiter()
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
        val functionalTest by registering(JvmTestSuite::class) {
            useJUnitJupiter()
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
