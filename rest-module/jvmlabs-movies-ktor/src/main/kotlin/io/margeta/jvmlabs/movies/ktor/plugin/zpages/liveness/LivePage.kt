package io.margeta.jvmlabs.movies.ktor.plugin.zpages.liveness

import io.margeta.jvmlabs.movies.ktor.plugin.zpages.Configuration
import io.margeta.jvmlabs.movies.ktor.plugin.zpages.ProbingPage

class LivePage : ProbingPage(type = "liveness")

fun Configuration.live(endpoint: String = "/livez", configure: LivePage.() -> Unit): LivePage =
    register(endpoint, LivePage().apply(configure))
