pluginManagement {
    repositories {
        includeBuild("../build-conventions")
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            version("jgit", "7.3.0.202506031305-r")
            version("nightconfig", "3.8.3")
            version("snakeyaml", "1.33")
            library("jgit", "org.eclipse.jgit", "org.eclipse.jgit").versionRef("jgit")
            library("nightconfig-toml", "com.electronwill.night-config", "toml").versionRef("nightconfig")
            library("nightconfig-yaml", "com.electronwill.night-config", "yaml").versionRef("nightconfig")
            library("nightconfig-hocon", "com.electronwill.night-config", "hocon").versionRef("nightconfig")
            library("nightconfig-json", "com.electronwill.night-config", "json").versionRef("nightconfig")
            library("snakeyaml", "org.yaml", "snakeyaml").versionRef("snakeyaml")
            bundle("nightconfig", listOf("nightconfig-toml", "nightconfig-yaml", "nightconfig-hocon", "nightconfig-json", "snakeyaml"))
        }
        create("testLibs") {
            version("groovy", "4.0.27")
            version("spock", "2.4-M6-groovy-4.0")
            library("groovy", "org.apache.groovy", "groovy-all").versionRef("groovy")
            library("spock-core", "org.spockframework", "spock-core").versionRef("spock")
            bundle("spock", listOf("spock_core"))
        }
    }
}

include(":semantic-version")

rootProject.name = "build-logic"
rootProject.children.forEach {
    it.buildFileName = "${it.name}.gradle.kts"
}
