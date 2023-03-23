import jvmlabs.build.version.SemanticVersionExtension

plugins {
    id("jvmlabs.build.kotlin-spring-conventions")
}

description = "GraphQL Spring laboratory"

val semanticVersion = rootProject.the<SemanticVersionExtension>()
val imageName = "zmargeta/graphql-spring"
val imageTag by lazy { semanticVersion.value.get().toString() }

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation(libs.mongodb.driver.reactive)
    implementation(libs.ulidcreator)
    testImplementation("org.springframework.graphql:spring-graphql-test")
}

tasks.processResources {
    from("src/main/graphql") {
        exclude("requests/")
        include("**/*.graphqls")
        into("graphql")
    }
}

jib {
    from {
        image = "bellsoft/liberica-openjre-alpine:19.0.2-9" +
            "@sha256:91c2c381b6249edee64cb929f5ca8b1e30702cee56d33e05713d43a23c3eecbc"
    }
    to {
        image = imageName
        tags = setOf(imageTag)
    }
//    container {
//        creationTime = imageCreationTime.map { it.toString() }
//    }
}

tasks.bootBuildImage {
    imageName = provider { "${imageName}:${imageTag}" }
}
