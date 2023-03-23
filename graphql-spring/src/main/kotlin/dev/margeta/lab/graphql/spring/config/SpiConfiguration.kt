package dev.margeta.lab.graphql.spring.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.connection.netty.NettyStreamFactoryFactory
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoDatabase
import dev.margeta.lab.graphql.spring.config.MongoProperties.Constants.DEFAULT_HOST
import dev.margeta.lab.graphql.spring.config.MongoProperties.Constants.DEFAULT_PORT
import dev.margeta.lab.graphql.spring.config.MongoProperties.Constants.DEFAULT_URI
import io.netty.channel.nio.NioEventLoopGroup
import jakarta.annotation.PreDestroy
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MongoProperties::class)
class SpiConfiguration {
    private val eventLoopGroupDelegate = lazy { NioEventLoopGroup() }
    private val eventLoopGroup by eventLoopGroupDelegate

    @Volatile
    private var mongoClient: MongoClient? = null

    @PreDestroy
    fun preDestroy() {
        if (eventLoopGroupDelegate.isInitialized()) {
            eventLoopGroup
                .shutdownGracefully()
                .awaitUninterruptibly(5000L)
        }
        mongoClient?.close()
    }

    @Bean
    fun eventLoopGroup(): NioEventLoopGroup = eventLoopGroup

    @Bean
    fun streamFactoryFactory(eventLoopGroup: NioEventLoopGroup): NettyStreamFactoryFactory =
        NettyStreamFactoryFactory
            .builder()
            .eventLoopGroup(eventLoopGroup)
            .build()

    @Bean
    fun mongoClientSettings(
        streamFactoryFactory: NettyStreamFactoryFactory,
        properties: MongoProperties,
    ): MongoClientSettings =
        MongoClientSettings
            .builder()
            .streamFactoryFactory(streamFactoryFactory)
            .applyHostAndPort(properties)
            .applyCredentials(properties)
            .applyReplicaSet(properties)
            .applyTimeouts(properties)
            .build()

    private fun MongoClientSettings.Builder.applyHostAndPort(properties: MongoProperties): MongoClientSettings.Builder {
        if (properties.uri != null) {
            return applyConnectionString(ConnectionString(properties.uri))
        }
        if (properties.host != null || properties.port != null) {
            val host = properties.host ?: DEFAULT_HOST
            val port = properties.port ?: DEFAULT_PORT
            val serverAddresses = buildList {
                add(ServerAddress(host, port))
                addAll(properties.additionalHosts.map(::ServerAddress))
            }
            return applyToClusterSettings {
                it.hosts(serverAddresses)
            }
        }
        return applyConnectionString(ConnectionString(DEFAULT_URI))
    }

    private fun MongoClientSettings.Builder.applyCredentials(properties: MongoProperties): MongoClientSettings.Builder {
        if (properties.uri == null && properties.username != null && properties.password != null) {
            val database = properties.authenticationDatabase ?: properties.clientDatabase
            credential(
                MongoCredential.createCredential(
                    properties.username,
                    database,
                    properties.password.toCharArray()
                )
            )
        }
        return this
    }

    private fun MongoClientSettings.Builder.applyReplicaSet(properties: MongoProperties): MongoClientSettings.Builder {
        if (properties.replicaSetName != null) {
            applyToClusterSettings {
                it.requiredReplicaSetName(properties.replicaSetName)
            }
        }
        return this
    }

    private fun MongoClientSettings.Builder.applyTimeouts(properties: MongoProperties): MongoClientSettings.Builder {
        if (properties.connectTimeoutMillis != null) {
            applyToSocketSettings {
                it.connectTimeout(properties.connectTimeoutMillis, TimeUnit.MILLISECONDS)
            }
        }
        if (properties.serverSelectionTimeoutMillis != null) {
            applyToClusterSettings {
                it.serverSelectionTimeout(properties.serverSelectionTimeoutMillis, TimeUnit.MILLISECONDS)
            }
        }
        return this
    }

    @Bean
    fun mongoClient(settings: MongoClientSettings): MongoClient {
        val mongoClient = this.mongoClient
        if (mongoClient == null) {
            this.mongoClient = MongoClients.create(settings)
        }
        return this.mongoClient ?: error("mongoClient set to null by another thread.")
    }

    @Bean
    fun mongoDatabase(mongoClient: MongoClient, properties: MongoProperties): MongoDatabase =
        mongoClient.getDatabase(properties.clientDatabase)
}
