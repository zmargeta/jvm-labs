package io.margeta.jvmlabs.movies.ktor

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.margeta.jvmlabs.movies.ktor.plugin.zpages.ProbeStatus.NotOk
import io.margeta.jvmlabs.movies.ktor.plugin.zpages.ProbeStatus.Ok
import io.margeta.jvmlabs.movies.ktor.plugin.zpages.live
import io.margeta.jvmlabs.movies.ktor.plugin.zpages.ready
import io.margeta.jvmlabs.movies.ktor.plugin.zpages.zPages

fun main(args: Array<String>) =
    EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    zPages {
        ready {
            probe("mongodb") { Ok }
            probe("redis") { Ok }
            probe("kafka") { Ok }
        }
        live {
            probe("mongodb") { NotOk }
            probe("redis") { Ok }
            probe("kafka") { Ok }
        }
    }

    routing {
        get("/movies") {
            log.atDebug().addKeyValue("key", "value").log("List of movies requested")
            call.respond(mapOf("movies" to listOf("The Godfather", "The Dark Knight")))
        }
    }
}
