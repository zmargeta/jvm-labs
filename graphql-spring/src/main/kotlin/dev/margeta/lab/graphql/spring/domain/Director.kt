package dev.margeta.lab.graphql.spring.domain

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.NonEmptyList
import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate
import dev.margeta.lab.graphql.spring.domain.Validation.Argument

data class Director private constructor(
    val id: DirectorId,
    val firstName: NonEmptyString,
    val lastName: NonEmptyString,
    val rating: Rating,
) {
    fun upVote(): Director = copy(rating = rating.upVote())

    fun downVote(): Director = copy(rating = rating.downVote())

    override fun equals(other: Any?): Boolean = other is Director && id == other.id

    override fun hashCode(): Int = id.hashCode()

    companion object {
        operator fun invoke(
            id: String,
            firstName: String,
            lastName: String,
            rating: Int = 0,
        ): Either<NonEmptyList<Validation>, Director> = either {
            zipOrAccumulate(
                { DirectorId(id).mapLeft { Argument("id", it) }.bind() },
                { NonEmptyString(firstName).mapLeft { Argument("firstName", it) }.bind() },
                { NonEmptyString(lastName).mapLeft { Argument("lastName", it) }.bind() },
                { Rating(rating).mapLeft { Argument("rating", it) }.bind() },
                { a, b, c, d ->
                    Director(
                        id = a,
                        firstName = b,
                        lastName = c,
                        rating = d,
                    )
                }
            )
        }
    }
}

@JvmInline
value class DirectorId private constructor(private val value: String) {
    override fun toString(): String = value

    companion object {
        operator fun invoke(value: String): Either<Validation, DirectorId> = when {
            value.isBlank() -> Left(Validation.BlankString)
            else -> Right(DirectorId(value))
        }
    }
}
