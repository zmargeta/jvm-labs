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
    fun visit(nodes: Set<ProjectDescriptor>) {
        nodes.forEach {
            if (it.children.isEmpty()) {
                configure(it)
                return
            }
            visit(it.children)
        }
    }

    visit(this@subprojects.children)
}

include(":rest-module:jvmlabs-movies-ktor")

rootProject.name = "jvm-labs"
rootProject.subprojects {
    buildFileName = "${name}.gradle.kts"
}
