package dev.margeta.lab.graphql.spring.domain

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.NonEmptyList
import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate
import dev.margeta.lab.graphql.spring.domain.Validation.Argument
import dev.margeta.lab.graphql.spring.domain.Validation.BlankString
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

data class Hero private constructor(
    val id: HeroId,
    val name: NonEmptyString,
    val heightMeters: Height,
    val lightSaber: LightSaber,
    val order: Order,
    val rating: Rating,
    val episodeIds: PersistentList<EpisodeId>, // TODO: move this to episode somehow
) {
    fun upVote(): Hero = copy(rating = rating.upVote())

    fun downVote(): Hero = copy(rating = rating.downVote())

    override fun equals(other: Any?): Boolean = other is Hero && other.id == id

    override fun hashCode(): Int = id.hashCode()

    companion object {
        operator fun invoke(
            id: String,
            name: String,
            heightMeters: Int,
            lightSaber: LightSaber,
            order: Order,
            rating: Int = 0,
            episodeIds: List<EpisodeId> = emptyList(),
        ): Either<NonEmptyList<Validation>, Hero> = either {
            zipOrAccumulate(
                { HeroId(id).mapLeft { Argument("id", it) }.bind() },
                { NonEmptyString(name).mapLeft { Argument("name", it) }.bind() },
                { Height(heightMeters).mapLeft { Argument("heightMeters", it) }.bind() },
                { Rating(rating).mapLeft { Argument("rating", it) }.bind() },
                { a, b, c, d ->
                    Hero(
                        id = a,
                        name = b,
                        heightMeters = c,
                        lightSaber = lightSaber,
                        order = order,
                        rating = d,
                        episodeIds = episodeIds.toPersistentList(),
                    )
                }
            )
        }
    }
}

@JvmInline
value class HeroId private constructor(private val value: String) {
    override fun toString(): String = value

    companion object {
        operator fun invoke(value: String): Either<Validation, HeroId> = when {
            value.isBlank() -> Left(BlankString)
            else -> Right(HeroId(value))
        }
    }
}

@JvmInline
value class Height private constructor(private val value: Int) {
    fun toInt(): Int = value

    override fun toString(): String = value.toString(radix = 10)

    companion object {
        operator fun invoke(value: Int): Either<Validation, Height> = when {
            value <= 0 -> Left(Validation.OutOfBounds(0, Int.MAX_VALUE))
            else -> Right(Height(value))
        }
    }
}

enum class LightSaber {
    Amethyst,
    Black,
    Blue,
    Cyan,
    Green,
    Indigo,
    Magenta,
    Orange,
    Red,
    White,
    Yellow,
}

enum class Order {
    JediOrder,
    Sith,
}
