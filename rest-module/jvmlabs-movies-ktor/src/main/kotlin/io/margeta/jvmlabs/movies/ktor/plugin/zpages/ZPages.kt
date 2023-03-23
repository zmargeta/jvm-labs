package io.margeta.jvmlabs.movies.ktor.plugin.zpages

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

object CallSetup : Hook<suspend (ApplicationCall) -> Boolean> {
    override fun install(pipeline: ApplicationCallPipeline, handler: suspend (ApplicationCall) -> Boolean) {
        pipeline.intercept(ApplicationCallPipeline.Setup) {
            if (handler(call)) {
                finish()
            }
        }
    }
}

val ZPages = createApplicationPlugin(name = "ZPages", createConfiguration = ::ZPagesConfig) {
    on(CallSetup) { call ->
        val zPage = pluginConfig[call.request.path()] ?: return@on false
        return@on zPage.respond(call)
    }
}

interface Configuration {
    fun <T : ZPage> register(endpoint: String, zPage: T): T
}

fun interface ZPage {
    suspend fun respond(call: ApplicationCall): Boolean
}

class ZPagesConfig : Configuration {
    private val pageRegistry: MutableMap<String, ZPage> = mutableMapOf()

    override fun <T : ZPage> register(endpoint: String, zPage: T): T {
        require(pageRegistry[endpoint] == null) { "Diagnostic endpoint $endpoint is already registered" }
        pageRegistry[endpoint] = zPage
        return zPage
    }

    internal operator fun get(endpoint: String): ZPage? = pageRegistry[endpoint]
}

abstract class ProbingPage(val type: String = "custom") : ZPage {
    private val probes: MutableList<Pair<String, suspend () -> Boolean>> = mutableListOf()

    fun probe(name: String, action: suspend () -> Boolean) {
        require(probes.none { (itName, _) -> itName == name }) { "$name $type probe is already registered" }
        probes.add(name to action)
    }

    override suspend fun respond(call: ApplicationCall): Boolean {
        val verbose = call.request.queryParameters.contains("verbose")

        val probes = launchProbes()
        val passed = probes.all { (_, result) -> result }

        if (verbose) {
            val body = buildString {
                probes.forEach { (name, result) ->
                    appendLine("[${if (result) "+" else "-"}]$name ${if (result) "ok" else "not ok"}")
                }
                appendLine("$type check ${if (passed) "passed" else "failed"}")
            }
            call.respondText(body, ContentType.Text.Plain)
            return true
        }

        call.respond(if (passed) HttpStatusCode.NoContent else HttpStatusCode.FailedDependency)
        return true
    }

    private suspend fun launchProbes(context: CoroutineContext = Dispatchers.IO): List<Pair<String, Boolean>> =
        coroutineScope {
            probes
                .withIndex()
                .map { (idx, probe) ->
                    async(context) {
                        val (name, action) = probe
                        idx to (name to runCatching { action.invoke() }.getOrDefault(false))
                    }
                }
                .awaitAll()
                .sortedBy { it.first }
                .map { it.second }
        }
}

fun Application.zPages(configure: Configuration.() -> Unit) = install(ZPages, configure)
