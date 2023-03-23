package dev.margeta.lab.graphql.spring.app

fun interface IdentityService {
    suspend fun nextString(): String
}
