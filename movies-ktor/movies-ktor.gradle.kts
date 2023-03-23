import jvmlabs.build.version.SemanticVersionExtension

plugins {
    id("jvmlabs.build.kotlin-conventions")
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
}

description = "Ktor REST Lab"

val semanticVersion = rootProject.the<SemanticVersionExtension>()

dependencies {
    implementation(libs.bundles.ktor)
    implementation(libs.logback)
    implementation(libs.ulidcreator)
}

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}
