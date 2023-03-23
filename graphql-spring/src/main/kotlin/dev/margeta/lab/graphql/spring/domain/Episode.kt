package dev.margeta.lab.graphql.spring.domain

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.NonEmptyList
import arrow.core.raise.either
import arrow.core.raise.mapOrAccumulate
import arrow.core.raise.zipOrAccumulate
import dev.margeta.lab.graphql.spring.domain.Validation.Argument
import dev.margeta.lab.graphql.spring.domain.Validation.BlankString
import dev.margeta.lab.graphql.spring.domain.Validation.OutOfBounds
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import java.time.LocalDate

data class Episode private constructor(
    val id: EpisodeId,
    val number: EpisodeNumber,
    val title: NonEmptyString,
    val directorId: DirectorId,
    val releaseDate: LocalDate,
    val rating: Rating,
    val heroIds: PersistentList<HeroId> = persistentListOf(),
) {
    fun upVote(): Episode = copy(rating = rating.upVote())

    fun downVote(): Episode = copy(rating = rating.downVote())

    override fun equals(other: Any?): Boolean = other is Episode && id == other.id

    override fun hashCode(): Int = id.hashCode()

    companion object {
        operator fun invoke(
            id: String,
            number: Int,
            title: String,
            directorId: String? = null,
            releaseDate: LocalDate,
            rating: Int = 0,
            heroIds: List<String> = emptyList(),
        ): Either<NonEmptyList<Validation>, Episode> = either {
            zipOrAccumulate(
                { EpisodeId(id).mapLeft { Argument("id", it) }.bind() },
                { EpisodeNumber(number).mapLeft { Argument("number", it) }.bind() },
                { NonEmptyString(title).mapLeft { Argument("title", it) }.bind() },
                { DirectorId(id).mapLeft { Argument("directorId", it) }.bind() },
                { Rating(rating).mapLeft { Argument("rating", it) }.bind() },
                {
                    val ids = mapOrAccumulate(heroIds.withIndex()) { id ->
                        HeroId(id.value).mapLeft { Argument("heroIds[${id.index}]", it) }.bind()
                    }
                    ids.toPersistentList()
                },
                { a, b, c, d, e, f ->
                    Episode(
                        id = a,
                        number = b,
                        title = c,
                        directorId = d,
                        releaseDate = releaseDate,
                        rating = e,
                        heroIds = f,
                    )
                }
            )
        }
    }
}

@JvmInline
value class EpisodeId private constructor(private val value: String) {
    override fun toString(): String = value

    companion object {
        operator fun invoke(value: String): Either<Validation, EpisodeId> = when {
            value.isBlank() -> Left(BlankString)
            else -> Right(EpisodeId(value))
        }
    }
}

@JvmInline
value class EpisodeNumber private constructor(private val value: Int) {
    fun toInt(): Int = value

    override fun toString(): String = value.toString(radix = 10)

    companion object {
        operator fun invoke(value: Int): Either<Validation, EpisodeNumber> = when (value) {
            !in 1..9 -> Left(OutOfBounds(1, 9))
            else -> Right(EpisodeNumber(value))
        }
    }
}
