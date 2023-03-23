package io.margeta.jvmlabs.movies.ktor

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.margeta.jvmlabs.movies.ktor.plugin.zpages.liveness.live
import io.margeta.jvmlabs.movies.ktor.plugin.zpages.readiness.ready
import io.margeta.jvmlabs.movies.ktor.plugin.zpages.zPages

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    zPages {
        ready {
            probe("mongodb") { true }
        }
        live {
            probe("mongodb") { false }
        }
    }

    routing {
        get("/movies") {
            log.atDebug().addKeyValue("key", "value").log("List of movies requested")
            call.respond(mapOf("movies" to listOf("The Godfather", "The Dark Knight")))
        }
    }
}
