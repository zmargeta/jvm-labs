import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("jvmlabs.build.common-conventions")
    id("org.springframework.boot")
}

val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
val testLibs = project.extensions.getByType<VersionCatalogsExtension>().named("testLibs")

dependencies {
    implementation(platform(libs.findLibrary("spring.boot.bom").orElseThrow()))
    implementation(platform(libs.findLibrary("spring.cloud.kubernetes.bom").orElseThrow()))
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.cloud:spring-cloud-starter-kubernetes-client-config")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}

configurations.implementation {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
}

tasks.named<BootJar>("bootJar") {
    manifest {
        val sysProps = System.getProperties()
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Automatic-Module-Name" to project.name.replace("-", "."),
            "Created-By" to "${sysProps["java.version"]} (${sysProps["java.specification.vendor"]})",
        )
    }
}
