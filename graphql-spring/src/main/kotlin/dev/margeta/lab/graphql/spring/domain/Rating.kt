package dev.margeta.lab.graphql.spring.domain

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import dev.margeta.lab.graphql.spring.domain.Validation.OutOfBounds

@JvmInline
value class Rating private constructor(private val value: Int) {
    fun upVote(): Rating =
        if (value == 500) this
        else Rating(value + 1)

    fun downVote(): Rating =
        if (value == 1) this
        else Rating(value - 1)

    fun toInt(): Int = value

    override fun toString(): String = value.toString(radix = 10)

    companion object {
        operator fun invoke(value: Int): Either<Validation, Rating> = when (value) {
            !in 0..500 -> Left(OutOfBounds(0, 500))
            else -> Right(Rating(value))
        }
    }
}
