package io.margeta.jvmlabs.movies.ktor.plugin.zpages

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.*

enum class ProbeStatus(val symbol: String, val message: String) {
    Ok("[+]", "ok"),
    NotOk("[-]", "not_ok")
}

typealias ProbeHandler = suspend () -> ProbeStatus

private typealias Probes = List<Pair<RoutingPath, ProbeHandler>>

private typealias ProbeResults = List<Pair<RoutingPath, ProbeStatus>>

data class ProbingPage(val type: String, val probes: Probes) : Page {
    override suspend fun handle(context: PageContext) {
        val (status, results) = launchProbes(probes)

        if (context.call.request.isVerbose) {
            context.call.respondText(
                text = buildResponseText(status, results),
                contentType = ContentType.Text.Plain.withCharset(Charsets.UTF_8),
                status = status.toHttpStatusCode()
            )
            return
        }

        if (status == ProbeStatus.Ok) {
            context.call.respond(HttpStatusCode.NoContent)
            return
        }

        context.call.respondText(
            text = buildResponseText(status),
            contentType = ContentType.Text.Plain.withCharset(Charsets.UTF_8),
            status = status.toHttpStatusCode()
        )
    }

    private suspend fun launchProbes(
        probes: Probes,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): Pair<ProbeStatus, ProbeResults> =
        coroutineScope {
            val results = probes
                .map { (path, handler) ->
                    async(context = dispatcher) {
                        path to runCatching { handler.invoke() }.getOrDefault(ProbeStatus.NotOk)
                    }
                }
                .awaitAll()
            val status = results.fold(ProbeStatus.Ok) { acc, (_, next) ->
                if (next == ProbeStatus.NotOk) ProbeStatus.NotOk
                else acc
            }
            status to results
        }

    private val ApplicationRequest.isVerbose
        get() = queryParameters.contains("verbose")

    private fun buildResponseText(overallStatus: ProbeStatus, results: ProbeResults = emptyList()): String =
        buildString {
            results.forEach { (path, status) ->
                appendLine("${status.symbol}${path.segments.joinToString(separator = "/")} ${status.message}")
            }
            appendLine("$type check ${if (overallStatus == ProbeStatus.Ok) "passed" else "failed"}")
        }

    private fun ProbeStatus.toHttpStatusCode(): HttpStatusCode =
        when (this) {
            ProbeStatus.Ok -> HttpStatusCode.OK
            ProbeStatus.NotOk -> HttpStatusCode.ServiceUnavailable
        }
}

class ProbingPageBuilder(val type: String = "custom") {
    private var probes: Map<RoutingPath, ProbeHandler> = mapOf()

    fun probe(name: String, handler: ProbeHandler) {
        val path = RoutingPath(name)
        require(path !in probes.keys) { "$name $type probe is already registered" }
        probes = probes + (path to handler)
    }

    fun toPage(): Page =
        ProbingPage(type, probes.toList())
}

fun ZPagesConfig.live(endpoint: String = "/livez", configure: ProbingPageBuilder.() -> Unit) =
    register(endpoint, ProbingPageBuilder("liveness").apply(configure).toPage())

fun ZPagesConfig.ready(endpoint: String = "/readyz", configure: ProbingPageBuilder.() -> Unit) =
    register(endpoint, ProbingPageBuilder("readiness").apply(configure).toPage())
