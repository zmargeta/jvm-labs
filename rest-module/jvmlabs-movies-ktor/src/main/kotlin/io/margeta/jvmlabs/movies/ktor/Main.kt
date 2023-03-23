package io.margeta.jvmlabs.movies.ktor

import io.ktor.serialization.kotlinx.cbor.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.ExperimentalSerializationApi

fun main(args: Array<String>) = EngineMain.main(args)

@OptIn(ExperimentalSerializationApi::class)
fun Application.module() {
    install(ContentNegotiation) {
        cbor()
        json()
    }

    routing {
        get("/") {
            call.respondText("Hello, world!")
        }
        get("/movies") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}
