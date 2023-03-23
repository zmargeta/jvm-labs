import io.margeta.jvmlabs.build.extensions.toImageTag
import io.margeta.jvmlabs.build.extensions.toJavaVersion

plugins {
    alias(libs.plugins.conventions.kotlin)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
}

description = "Ktor Movie Service"

dependencies {
    implementation(libs.bundles.ktor)
    implementation(libs.logback)
}

ktor {
    fatJar {
        archiveFileName = "${project.name}-${project.version}-all.jar"
    }
    docker {
        localImageName = project.name
        imageTag = project.version.toImageTag
        jreVersion = java.toolchain.languageVersion.toJavaVersion
    }
}

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}
