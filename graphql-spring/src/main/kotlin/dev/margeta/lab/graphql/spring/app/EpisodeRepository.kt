package dev.margeta.lab.graphql.spring.app

import arrow.core.Either
import dev.margeta.lab.graphql.spring.domain.Episode
import dev.margeta.lab.graphql.spring.domain.EpisodeId
import dev.margeta.lab.graphql.spring.domain.Error

interface EpisodeRepository {
    suspend fun list(): Either<Error, List<Episode>>
    suspend fun get(id: EpisodeId): Either<Error, Episode>
    suspend fun create(episode: Episode): Either<Error, Episode>
    suspend fun save(episode: Episode): Either<Error, Episode>
    suspend fun delete(id: EpisodeId): Either<Error, Episode>
}
