package io.margeta.jvmlabs.movies.ktor.plugin.zpages.readiness

import io.margeta.jvmlabs.movies.ktor.plugin.zpages.Configuration
import io.margeta.jvmlabs.movies.ktor.plugin.zpages.ProbingPage

class ReadyPage : ProbingPage(type = "readiness")

fun Configuration.ready(endpoint: String = "/readyz", configure: ReadyPage.() -> Unit): ReadyPage =
    register(endpoint, ReadyPage().apply(configure))
