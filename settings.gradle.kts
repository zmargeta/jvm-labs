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

fun ProjectDescriptor.subprojects(block: ProjectDescriptor.() -> Unit) {
    val nodes = ArrayDeque(this@subprojects.children)
    while (nodes.isNotEmpty()) {
        val current = nodes.removeLast()
        if (current.children.isEmpty()) block(current)
        else current.children.forEach { nodes.addLast(it) }
    }
}

include(":rest-module:jvmlabs-movies-ktor")

rootProject.name = "jvm-labs"
rootProject.subprojects {
    buildFileName = "${name}.gradle.kts"
}
