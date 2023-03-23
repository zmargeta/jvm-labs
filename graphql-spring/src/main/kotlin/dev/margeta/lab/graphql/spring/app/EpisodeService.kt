package dev.margeta.lab.graphql.spring.app

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import arrow.core.raise.either
import dev.margeta.lab.graphql.spring.domain.Episode
import dev.margeta.lab.graphql.spring.domain.EpisodeId
import dev.margeta.lab.graphql.spring.domain.Error
import java.time.LocalDate

class EpisodeService(private val repository: EpisodeRepository, private val identityService: IdentityService) {
    suspend fun list(): Either<Error, List<Episode>> = repository.list()

    suspend fun get(id: EpisodeId): Either<Error, Episode> = repository.get(id)

    suspend fun create(
        number: Int,
        title: String,
        directorId: String,
        releaseDate: LocalDate,
    ): Either<NonEmptyList<Error>, Episode> = either {
        val episode =
            Episode(
                id = identityService.nextString(),
                number = number,
                title = title,
                directorId = directorId,
                releaseDate = releaseDate,
            )
        repository.create(episode.bind()).mapLeft(::nonEmptyListOf).bind()
    }

    suspend fun upVote(id: EpisodeId): Either<Error, Episode> = either {
        val episode = repository.get(id).bind()
        repository.save(episode.upVote()).bind()
    }

    suspend fun downVote(id: EpisodeId): Either<Error, Episode> = either {
        val episode = repository.get(id).bind()
        repository.save(episode.downVote()).bind()
    }

    suspend fun delete(id: EpisodeId): Either<Error, Episode> = repository.delete(id)
}
