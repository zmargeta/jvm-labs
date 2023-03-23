plugins {
    id("jvmlabs.build.kotlin-conventions")
    id("jvmlabs.build.spring-conventions")
    id("org.jetbrains.kotlin.plugin.spring")
}

val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
val testLibs = project.extensions.getByType<VersionCatalogsExtension>().named("testLibs")

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
