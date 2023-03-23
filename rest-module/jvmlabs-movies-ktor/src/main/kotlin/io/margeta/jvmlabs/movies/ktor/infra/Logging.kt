package io.margeta.jvmlabs.movies.ktor.infra

import ch.qos.logback.classic.spi.ILoggingEvent
import co.elastic.logging.JsonUtils
import co.elastic.logging.logback.EcsEncoder

class EcsEncoder : EcsEncoder() {
    override fun addCustomFields(event: ILoggingEvent?, builder: StringBuilder?) {
        event?.keyValuePairs
            ?.filter { it.key?.isNotBlank() == true }
            ?.forEach {
                builder?.append("\"custom.")
                JsonUtils.quoteAsString(it.key, builder)
                builder?.append("\":\"")
                JsonUtils.quoteAsString(it.value?.toString(), builder)
                builder?.append("\",")
            }
    }
}
