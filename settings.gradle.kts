pluginManagement {
    includeBuild("build-conventions")
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("testLibs") {
            from(files("gradle/test-libs.versions.toml"))
        }
    }
}

include(":graphql-spring")

rootProject.name = "jvm-labs"
rootProject.children.forEach {
    it.buildFileName = "${it.name}.gradle.kts"
}
