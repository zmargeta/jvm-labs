package dev.margeta.lab.graphql.spring.api

import arrow.core.NonEmptyList
import arrow.core.getOrElse
import arrow.core.raise.Raise
import arrow.core.raise.either
import dev.margeta.lab.graphql.spring.app.EpisodeService
import dev.margeta.lab.graphql.spring.domain.Error
import kotlinx.coroutines.reactor.mono
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import dev.margeta.lab.graphql.spring.api.Episode as ApiEpisode

@Controller
class EpisodeController(private val service: EpisodeService) {
    @QueryMapping
    fun episode(@Argument id: String): Mono<ApiEpisode> = mono {
        getRightOrThrow {
            service
                .get(id.toEpisodeId().bind())
                .bind()
                .toApiEpisode()
        }
    }

    private inline fun <A> getRightOrThrow(block: Raise<Error>.() -> A): A? =
        either { block.invoke(this) }.getOrElse {
            throw DomainException(it)
        }

    private inline fun <A> getRightListOrThrow(block: Raise<NonEmptyList<Error>>.() -> A): A? =
        either { block.invoke(this) }.getOrElse {
            throw DomainException(it)
        }

    @QueryMapping
    fun episodes(): Mono<List<ApiEpisode>> = mono {
        getRightOrThrow {
            service
                .list()
                .bind()
                .map { it.toApiEpisode() }
        }
    }

    @MutationMapping
    fun createEpisode(@Argument input: CreateEpisodeInput): Mono<ApiEpisode> = mono {
        getRightListOrThrow {
            service
                .create(
                    number = input.number,
                    title = input.title,
                    directorId = input.directorId,
                    releaseDate = input.releaseDate,
                )
                .bind()
                .toApiEpisode()
        }
    }

    @MutationMapping
    fun upVoteEpisode(@Argument id: String): Mono<ApiEpisode> = mono {
        getRightOrThrow {
            service
                .upVote(id.toEpisodeId().bind())
                .bind()
                .toApiEpisode()
        }
    }

    @MutationMapping
    fun downVoteEpisode(@Argument id: String): Mono<ApiEpisode> = mono {
        getRightOrThrow {
            service
                .downVote(id.toEpisodeId().bind())
                .bind()
                .toApiEpisode()
        }
    }

    @MutationMapping
    fun deleteEpisode(@Argument id: String): Mono<ApiEpisode> = mono {
        getRightOrThrow {
            service
                .delete(id.toEpisodeId().bind())
                .bind()
                .toApiEpisode()
        }
    }
}
