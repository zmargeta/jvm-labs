package dev.margeta.lab.graphql.spring.domain

sealed interface Error {
    object NotFound : Error
    data class Provider(val throwable: Throwable) : Error
}

sealed class Validation(val message: String) : Error {
    object EmptyString : Validation("value contain no characters")
    object BlankString : Validation("value is empty or consists solely of whitespace characters")
    data class OutOfBounds(val min: Int, val max: Int) : Validation("value is out of bounds [$min, $max]")
    data class Argument(val name: String, val error: Validation) : Validation(error.message)
}
