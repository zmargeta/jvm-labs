pluginManagement {
    repositories {
        includeBuild("../build-conventions")
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") {
            version("jgit", "6.5.0.202303070854-r")
            version("nightconfig", "3.6.6")
            library("jgit", "org.eclipse.jgit", "org.eclipse.jgit").versionRef("jgit")
            library("toml", "com.electronwill.night-config", "toml").versionRef("nightconfig")
            library("yaml", "com.electronwill.night-config", "yaml").versionRef("nightconfig")
            library("hocon", "com.electronwill.night-config", "hocon").versionRef("nightconfig")
            library("json", "com.electronwill.night-config", "json").versionRef("nightconfig")
            bundle("nightconfig", listOf("toml", "yaml", "hocon", "json"))
        }
        create("testLibs") {
            version("groovy", "3.0.15")
            version("spock_core", "2.3-groovy-3.0")
            version("bytebuddy", "1.14.1")
            version("objenesis", "3.3")
            library("groovy", "org.codehaus.groovy", "groovy-all").versionRef("groovy")
            library("spock_core", "org.spockframework", "spock-core").versionRef("spock_core")
            library("bytebuddy", "net.bytebuddy", "byte-buddy").versionRef("bytebuddy")
            library("objenesis", "org.objenesis", "objenesis").versionRef("objenesis")
            bundle("spock", listOf("spock_core", "bytebuddy", "objenesis"))
        }
    }
}

include(":semantic-version")

rootProject.name = "build-logic"
rootProject.children.forEach {
    it.buildFileName = "${it.name}.gradle.kts"
}
