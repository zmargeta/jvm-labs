package dev.margeta.lab.graphql.spring.domain

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import dev.margeta.lab.graphql.spring.domain.Validation.EmptyString

@JvmInline
value class NonEmptyString private constructor(private val value: String) {
    override fun toString(): String = value

    companion object {
        operator fun invoke(value: String): Either<Validation, NonEmptyString> = when {
            value.isEmpty() -> Left(EmptyString)
            else -> Right(NonEmptyString(value))
        }
    }
}
