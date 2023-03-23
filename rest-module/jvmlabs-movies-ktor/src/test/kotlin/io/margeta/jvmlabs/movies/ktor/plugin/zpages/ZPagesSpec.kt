package io.margeta.jvmlabs.movies.ktor.plugin.zpages

import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.margeta.jvmlabs.movies.ktor.plugin.zpages.RoutingPath.Companion.Root
import io.margeta.jvmlabs.movies.ktor.plugin.zpages.fixtures.*

class ZPagesSpec : FreeSpec({
})

@DisplayName("routing path")
class RoutingPathSpec : FreeSpec({
    "parses a URI path string" - {
        withData(
            nameFn = { (p, ss) -> "[p: $p, ss: $ss]" },
            "" to emptySegments(),
            " " to emptySegments(),
            "/" to emptySegments(),
            "//" to emptySegments(),
            "a" to segmentsOf("a"),
            "/a" to segmentsOf("a"),
            "a%20b" to segmentsOf("a b"),
            "a/b" to segmentsOf("a", "b"),
            "/a/b" to segmentsOf("a", "b"),
        ) { (p, ss) ->
            // given a URI path string
            val path = p

            // when parsing the URI path
            val actual = RoutingPath(path)

            // then makes an equivalent routing path
            actual shouldHaveSegments ss
        }
    }

    "returns its head segment" - {
        withData(
            nameFn = { (p, h) -> "[p: $p, h: $h]" },
            path("/a") to segment("a"),
            path("/a/b") to segment("a"),
        ) { (p, h) ->
            // given a routing path
            val path = p

            // when getting the routing path head
            val actual = path.head()

            // then returns the head segment
            actual shouldBe h
        }
    }

    "returns its tail routing path" - {
        withData(
            nameFn = { (p, t) -> "[p: $p, t: $t]" },
            path("/") to path("/"),
            path("/a") to path("/"),
            path("/a/b") to path("/b"),
        ) { (p, t) ->
            // given a routing path
            val path = p

            // when getting the routing path tail
            val actual = path.tail()

            // then returns the routing path tail
            actual shouldBe t
        }
    }
})

fun haveSegments(segments: List<RoutingPathSegment>): Matcher<RoutingPath> =
    Matcher { value ->
        MatcherResult(
            value.segments == segments,
            { "routing path should have segments $segments but was ${value.segments}" },
            { "routing path should not have segments $segments" },
        )
    }

infix fun RoutingPath.shouldHaveSegments(segments: List<RoutingPathSegment>): RoutingPath {
    this should haveSegments(segments)
    return this
}

@DisplayName("route")
class RouteSpec : FreeSpec({
    "adds child routes" - {
        withData(
            nameFn = { (a, ps) -> "[p: ${a.path}, ps: $ps]" },
            args(path("/"), handler()) to emptyPaths(),
            args(path("/a"), handler()) to pathsOf("/a"),
            args(path("/a/b"), handler()) to pathsOf("/a", "/a/b"),
        ) { (a, ps) ->
            // given a route
            val route = route()

            // and a routing path
            val path = a.path

            // and a page handler
            val handler = a.handler

            // when adding child routes to the route
            val actual = route + (path to handler)

            // then returns a route that contains the given child routes
            actual shouldContainExactly ps
        }
    }

    "gets the handler for a known route" - {
        // given a routing path
        val path = path("/a")

        // and a page handler
        val handler = handler()

        // and a route for it
        val route = route() + (path to handler)

        // when getting the route page handler for the given routing path
        val actual = route[path]!!

        // then returns the given handler and empty path info
        actual shouldHaveHandler handler
        actual.shouldHaveEmptyPathInfo()
    }

    "gets the handler and path info for a partially matched known route" - {
        // given a routing path
        val path = path("/a")

        // and a page handler
        val handler = handler()

        // and a route for it
        val route = route() + (path to handler)

        // when getting the route page handler for the given routing path child
        val actual = route[path + "/b"]!!

        // then returns the given handler and empty path info
        actual shouldHaveHandler handler
        actual shouldHavePathInfo path("/b")
    }

    "gets null for an unknown route" - {
        // given a routing path
        val path = path("/a")

        // and an empty route
        val route = route()

        // when getting the route page handler for the given routing path
        val actual = route[path + "/b"]

        // then returns null
        actual shouldBe null
    }
})

fun containExactly(expected: List<RoutingPath>): Matcher<Route> =
    Matcher { actual ->
        fun contains(route: Route, paths: List<RoutingPath>): Boolean {
            val childPaths = paths
                .filterNot { it == Root }
                .groupBy({ it.head() }, { it.tail() })
            val pathsDiff = (route.children.keys - childPaths.keys) union (childPaths.keys - route.children.keys)
            return pathsDiff.isEmpty() && route.children.all { (k, v) -> contains(v, childPaths.getValue(k)) }
        }

        MatcherResult(
            contains(actual, expected),
            { "route should contain the routes for paths $expected" },
            { "route should not contain the routes for paths $expected" },
        )
    }

infix fun Route.shouldContainExactly(expected: List<RoutingPath>): Route {
    this should containExactly(expected)
    return this
}

fun haveHandler(expected: PageHandler): Matcher<Pair<PageHandler, RoutingPath>> =
    Matcher { (actual, _) ->
        MatcherResult(
            actual == expected,
            { "result should have the given handler" },
            { "result should not have the given handler" },
        )
    }

infix fun Pair<PageHandler, RoutingPath>.shouldHaveHandler(expected: PageHandler): Pair<PageHandler, RoutingPath> {
    this should haveHandler(expected)
    return this
}

fun havePathInfo(expected: RoutingPath): Matcher<Pair<PageHandler, RoutingPath>> =
    Matcher { (_, actual) ->
        MatcherResult(
            actual == expected,
            { "result should have the path info $expected" },
            { "result should not have the path info $expected" },
        )
    }

fun Pair<PageHandler, RoutingPath>.shouldHaveEmptyPathInfo(): Pair<PageHandler, RoutingPath> {
    this should havePathInfo(Root)
    return this
}

infix fun Pair<PageHandler, RoutingPath>.shouldHavePathInfo(expected: RoutingPath): Pair<PageHandler, RoutingPath> {
    this should havePathInfo(expected)
    return this
}
