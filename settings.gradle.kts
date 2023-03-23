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

fun ProjectDescriptor.subprojects(configure: ProjectDescriptor.() -> Unit) {
    tailrec fun visit(nodes: ArrayDeque<ProjectDescriptor>) {
        if (nodes.isEmpty()) return

        val current = nodes.removeLast()
        if (current.children.isEmpty()) {
            configure(current)
            return
        }

        nodes.addAll(current.children)
        visit(nodes)
    }

    visit(ArrayDeque(this@subprojects.children))
}

include(":rest-module:jvmlabs-movies-ktor")

rootProject.name = "jvm-labs"
rootProject.subprojects {
    buildFileName = "${name}.gradle.kts"
}
