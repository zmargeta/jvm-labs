import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("io.margeta.jvmlabs.build.common-conventions")
    id("org.jetbrains.kotlin.jvm")
}

val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
val testLibs = project.extensions.getByType<VersionCatalogsExtension>().named("testLibs")

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation(libs.findLibrary("kotlinx.coroutines.core").orElseThrow())
    implementation(libs.findLibrary("kotlinx.collections.immutable").orElseThrow())
}

tasks.compileKotlin {
    compilerOptions {
        apiVersion = KotlinVersion.KOTLIN_2_2
        languageVersion = KotlinVersion.KOTLIN_2_2
        allWarningsAsErrors = true
        freeCompilerArgs = listOf(
            "-Xsuppress-version-warnings",
            "-Xjsr305=strict",
            "-opt-in=kotlin.RequiresOptIn",
            "-Xcontext-parameters",
            "-Xconsistent-data-class-copy-visibility",
        )
    }
}

sourceSets {
    testFixtures {
        kotlin {
            setSrcDirs(listOf("src/test-fixtures/kotlin"))
        }
    }
}

testing {
    suites {
        configureEach {
            if (this is JvmTestSuite) {
                dependencies {
                    implementation(project())
                    implementation.bundle(testLibs.findBundle("kotest").orElseThrow())
                }
            }
        }
        val integrationTest by getting(JvmTestSuite::class) {
            sources {
                kotlin {
                    setSrcDirs(listOf("src/integration-test/kotlin"))
                }
            }
        }
        val functionalTest by getting(JvmTestSuite::class) {
            sources {
                kotlin {
                    setSrcDirs(listOf("src/functional-test/kotlin"))
                }
            }
        }
    }
}
