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
        imageTag = project.version.toString()
        jreVersion = provider { JavaVersion.toVersion(java.toolchain.languageVersion.get().asInt()) }
    }
}

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}
