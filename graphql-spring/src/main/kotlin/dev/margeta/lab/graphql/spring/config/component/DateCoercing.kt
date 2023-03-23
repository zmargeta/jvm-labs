package dev.margeta.lab.graphql.spring.config.component

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.raise.either
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingSerializeException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import java.time.format.DateTimeParseException

object DateCoercing : Coercing<LocalDate, String> {
    override fun serialize(input: Any): String =
        when (input) {
            is LocalDate -> input.toIsoString()
            is LocalDateTime -> input.toIsoString()
            is String ->
                input
                    .toLocalDate()
                    .map(::serialize)
                    .getOrElse {
                        fail(expectedType = "Date", actualType = input.javaClass.simpleName)
                    }

            else -> fail(expectedType = "Date", actualType = input.javaClass.simpleName)
        }

    private fun LocalDate.toIsoString(): String = format(ISO_LOCAL_DATE)

    private fun LocalDateTime.toIsoString(): String = format(ISO_LOCAL_DATE)

    private fun String.toLocalDate(): Either<DateTimeParseException, LocalDate> = either {
        LocalDate.parse(this@toLocalDate, ISO_LOCAL_DATE)
    }

    private fun fail(expectedType: String, actualType: String): Nothing =
        throw CoercingSerializeException("Expected type '$expectedType' but was '$actualType'.")

    override fun parseValue(input: Any): LocalDate =
        if (input is String) {
            input.toLocalDate().getOrElse {
                fail(expectedType = "String", actualType = input.javaClass.simpleName)
            }
        } else {
            fail(expectedType = "String", actualType = input.javaClass.simpleName)
        }

    override fun parseLiteral(input: Any): LocalDate =
        if (input is StringValue) {
            input.value.toLocalDate().getOrElse {
                fail(expectedType = "StringValue", actualType = input.javaClass.simpleName)
            }
        } else {
            fail(expectedType = "StringValue", actualType = input.javaClass.simpleName)
        }
}
