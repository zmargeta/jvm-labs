package io.margeta.jvmlabs.movies.ktor.plugin.zpages

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.util.pipeline.*

val ZPages = createApplicationPlugin(name = "ZPages", createConfiguration = ::ZPagesConfig) {
    val phase = PipelinePhase("ZPages")
    val httpMethods = setOf(HttpMethod.Get)
    val contentTypes = setOf(ContentType.Any, ContentType.Text.Plain)
    val handlers = Handlers(pluginConfig)

    application.insertPhaseBefore(ApplicationCallPipeline.Plugins, phase)
    application.intercept(phase) {
        if (!call.isHandled && call.request.httpMethod in httpMethods && call.request.contentType() in contentTypes) {
            val (handler, pathInfo) = handlers[call.request.path()] ?: return@intercept
            handler.invoke(PageContext(call, pathInfo))
        }
    }
}

data class PageContext(
    val call: ApplicationCall,
    val pathInfo: RoutingPath,
)

typealias PageHandler = suspend PageContext.() -> Unit

class ZPagesConfig {
    internal var rootRoute: Route = Route()

    fun register(endpoint: String, page: Page) {
        val path = RoutingPath(endpoint)
        require(path !in rootRoute) { "Diagnostic endpoint $endpoint is already registered" }
        rootRoute += path to { page.handle(this) }
    }
}

class Handlers private constructor(private val route: Route) {
    operator fun get(path: String): Pair<PageHandler, RoutingPath>? =
        route[RoutingPath(path)]

    companion object {
        operator fun invoke(config: ZPagesConfig): Handlers =
            Handlers(config.rootRoute)
    }
}

data class RoutingPath private constructor(val segments: List<RoutingPathSegment>) {
    override fun toString(): String =
        segments.joinToString(separator = "/", prefix = "/")

    fun head(): RoutingPathSegment =
        segments.first()

    fun tail(): RoutingPath =
        if (segments.size < 2) Root
        else copy(segments = segments.drop(1))

    companion object {
        val Root: RoutingPath = RoutingPath(emptyList())

        operator fun invoke(path: String): RoutingPath {
            if (path == "/") return Root
            val segments = path
                .splitToSequence("/")
                .filter { it.isNotBlank() }
                .map { RoutingPathSegment(it.decodeURLPart()) }
            return RoutingPath(segments.toList())
        }
    }
}

@JvmInline
value class RoutingPathSegment(private val value: String) {
    override fun toString(): String = value
}

data class Route(
    val children: Map<RoutingPathSegment, Route> = emptyMap(),
    val handler: PageHandler? = null,
) {
    operator fun contains(path: RoutingPath): Boolean =
        get(path) != null

    operator fun get(path: RoutingPath): Pair<PageHandler, RoutingPath>? {
        if (path == RoutingPath.Root) return handler?.let { it to RoutingPath.Root }
        val child = children[path.head()] ?: return handler?.let { it to path }
        return child[path.tail()]
    }

    operator fun plus(mapping: Pair<RoutingPath, PageHandler>): Route {
        val (path, handler) = mapping
        if (path == RoutingPath.Root) return copy(handler = handler)
        val child = children
            .getOrElse(path.head()) { Route() }
            .run { plus(path.tail() to handler) }
        return copy(children = children + (path.head() to child))
    }
}

fun interface Page {
    suspend fun handle(context: PageContext)
}

fun Application.zPages(configure: ZPagesConfig.() -> Unit) =
    install(ZPages, configure)
