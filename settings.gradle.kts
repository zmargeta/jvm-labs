fun ProjectDescriptor.subprojects(action: (ProjectDescriptor) -> Unit) {
    val nodes = ArrayDeque(this@subprojects.children)
    while (nodes.isNotEmpty()) {
        val current = nodes.removeLast()
        if (current.children.isEmpty()) action(current)
        else current.children.forEach { nodes.addLast(it) }
    }
}

pluginManagement {
    includeBuild("build-conventions")
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("testLibs").from(files("gradle/test-libs.versions.toml"))
    }
}

include(":movies-module:movies-ktor")

rootProject.name = "jvm-labs"
rootProject.subprojects {
    it.buildFileName = "${it.name}.gradle.kts"
}
