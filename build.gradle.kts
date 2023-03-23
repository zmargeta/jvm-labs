import io.margeta.jvmlabs.build.version.task.VersionInfoTask

plugins {
    alias(libs.plugins.build.semanticversion)
}

configure(allprojects) {
    configurations.configureEach {
        group = "io.margeta.jvmlabs"
    }
    tasks.withType<ProcessResources>().configureEach {
        dependsOn(tasks.withType<VersionInfoTask>())
    }
}

description = "JVM Labs"

semanticVersion {
    majorFormat = "YY"
    minorFormat = "MM"
}
