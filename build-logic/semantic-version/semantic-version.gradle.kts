plugins {
    id("java-gradle-plugin")
    id("io.margeta.jvmlabs.build.java-conventions")
}

description = "Semantic version Gradle plugin"

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.jgit)
    implementation(libs.bundles.nightconfig)
    testFixturesApi(testLibs.bundles.spock)
    testFixturesImplementation(libs.jgit)
    integrationTestImplementation(libs.jgit)
    functionalTestImplementation(libs.jgit)
    functionalTestImplementation(libs.bundles.nightconfig)
}

gradlePlugin {
    testSourceSet(sourceSets.functionalTest.get())
    plugins {
        create("simplePlugin") {
            id = "io.margeta.jvmlabs.build.semantic-version"
            implementationClass = "io.margeta.jvmlabs.build.version.SemanticVersionPlugin"
        }
    }
}
