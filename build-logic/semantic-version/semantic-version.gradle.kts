plugins {
    id("java-gradle-plugin")
    id("jvmlabs.build.java-conventions")
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
            id = "jvmlabs.build.semantic-version"
            implementationClass = "jvmlabs.build.version.SemanticVersionPlugin"
        }
    }
}
