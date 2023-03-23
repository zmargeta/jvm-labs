import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("jvmlabs.build.common-conventions")
    id("org.jetbrains.kotlin.jvm")
}

val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
val testLibs = project.extensions.getByType<VersionCatalogsExtension>().named("testLibs")

dependencies {
    implementation(platform(libs.findLibrary("arrow.bom").orElseThrow()))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation(libs.findLibrary("kotlinx.collections.immutable").orElseThrow())
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-optics")
    implementation("io.arrow-kt:arrow-fx-coroutines")
    implementation("io.arrow-kt:arrow-fx-stm")
}

tasks.compileKotlin {
    compilerOptions {
        apiVersion = KotlinVersion.KOTLIN_1_8
        languageVersion = KotlinVersion.KOTLIN_1_8
        allWarningsAsErrors = true
        freeCompilerArgs = listOf(
            "-Xsuppress-version-warnings",
            "-Xjsr305=strict",
            "-opt-in=kotlin.RequiresOptIn",
            "-Xcontext-receivers",
        )
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
    }
}
