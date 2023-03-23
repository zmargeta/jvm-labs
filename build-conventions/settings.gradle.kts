pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
        create("testLibs") {
            from(files("../gradle/test-libs.versions.toml"))
        }
    }
}

rootProject.name = "build-conventions"
