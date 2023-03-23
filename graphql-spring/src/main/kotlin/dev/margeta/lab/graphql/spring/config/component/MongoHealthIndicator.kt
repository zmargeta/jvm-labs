package dev.margeta.lab.graphql.spring.config.component

import com.mongodb.reactivestreams.client.MongoDatabase
import org.bson.BsonDocument
import org.bson.BsonInt32
import org.springframework.boot.actuate.health.AbstractReactiveHealthIndicator
import org.springframework.boot.actuate.health.Health
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

private const val FAILED_MESSAGE = "Mongo health check failed."
private const val MAX_WIRE_VERSION_KEY = "maxWireVersion"

@Component
class MongoHealthIndicator(private val database: MongoDatabase) : AbstractReactiveHealthIndicator(FAILED_MESSAGE) {
    override fun doHealthCheck(healthBuilder: Health.Builder): Mono<Health> =
        database
            .runCommand(BsonDocument("hello", BsonInt32(1)))
            .toMono()
            .mapNotNull {
                healthBuilder
                    .up()
                    .withDetail(MAX_WIRE_VERSION_KEY, it.getInteger(MAX_WIRE_VERSION_KEY))
                    .build()
            }
            .onErrorReturn(healthBuilder.outOfService().build())
}
