package dev.margeta.lab.graphql.spring.api

import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import dev.margeta.lab.graphql.spring.domain.Error

class DomainException(val errors: NonEmptyList<Error>) : RuntimeException() {
    constructor(error: Error) : this(nonEmptyListOf(error))
}
