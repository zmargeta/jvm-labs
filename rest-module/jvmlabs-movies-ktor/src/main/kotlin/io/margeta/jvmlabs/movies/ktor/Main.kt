package io.margeta.jvmlabs.movies.ktor

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/livez") {
            log.atDebug().log("Liveness check requested")
            call.respondText("health check passed")
        }
        get("/readyz") {
            log.atDebug().log("Readiness check requested")
            call.respondText("health check passed")
        }
        get("/movies") {
            log.atDebug().addKeyValue("key", "value").log("List of movies requested")
            call.respond(mapOf("movies" to listOf("The Godfather", "The Dark Knight")))
        }
    }
}
