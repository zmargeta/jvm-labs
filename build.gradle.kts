import jvmlabs.build.version.task.VersionInfoTask

plugins {
    id("jvmlabs.build.semantic-version")
}

configure(allprojects) {
    configurations.configureEach {
        group = "dev.margeta.labs"
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
