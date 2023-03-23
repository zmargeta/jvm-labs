package dev.margeta.lab.graphql.spring.config

import com.mongodb.ConnectionString
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "mongodb")
data class MongoProperties(
    val host: String?,
    val port: Int?,
    val additionalHosts: List<String> = emptyList(),
    val uri: String?,
    val database: String?,
    val authenticationDatabase: String?,
    val username: String?,
    val password: String?,
    val replicaSetName: String?,
    val connectTimeoutMillis: Int?,
    val serverSelectionTimeoutMillis: Long?,
) {
    val clientDatabase: String
        get() = database ?: ConnectionString(uri ?: DEFAULT_URI).database ?: DEFAULT_DATABASE

    companion object Constants {
        const val DEFAULT_DATABASE = "test"
        const val DEFAULT_HOST = "localhost"
        const val DEFAULT_PORT = 27017
        const val DEFAULT_URI = "mongodb://localhost/$DEFAULT_DATABASE"
    }
}
