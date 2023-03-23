package io.margeta.jvmlabs.movies.ktor.plugin.zpages.fixtures

import io.margeta.jvmlabs.movies.ktor.plugin.zpages.PageHandler
import io.margeta.jvmlabs.movies.ktor.plugin.zpages.Route
import io.margeta.jvmlabs.movies.ktor.plugin.zpages.RoutingPath
import io.margeta.jvmlabs.movies.ktor.plugin.zpages.RoutingPathSegment

fun path(value: String): RoutingPath =
    RoutingPath(value)

operator fun RoutingPath.plus(value: String): RoutingPath =
    RoutingPath(toString() + value)

fun emptyPaths(): List<RoutingPath> =
    emptyList()

fun pathsOf(vararg values: String): List<RoutingPath> =
    values.map { path(it) }

fun segment(value: String): RoutingPathSegment =
    RoutingPathSegment(value)

fun emptySegments(): List<RoutingPathSegment> =
    emptyList()

fun segmentsOf(vararg values: String): List<RoutingPathSegment> =
    values.map { segment(it) }

fun route(): Route =
    Route()

fun handler(): PageHandler =
    {}

data class RoutePlusInput(val path: RoutingPath, val handler: PageHandler)

fun args(path: RoutingPath, handler: PageHandler): RoutePlusInput =
    RoutePlusInput(path, handler)
