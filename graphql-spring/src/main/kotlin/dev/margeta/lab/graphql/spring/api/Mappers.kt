package dev.margeta.lab.graphql.spring.api

import arrow.core.Either
import dev.margeta.lab.graphql.spring.domain.Episode
import dev.margeta.lab.graphql.spring.domain.EpisodeId
import dev.margeta.lab.graphql.spring.domain.EpisodeNumber
import dev.margeta.lab.graphql.spring.domain.NonEmptyString
import dev.margeta.lab.graphql.spring.domain.Validation
import dev.margeta.lab.graphql.spring.api.Episode as ApiEpisode

fun String.toNonEmptyString(): Either<Validation, NonEmptyString> = NonEmptyString(this)

fun String.toEpisodeId(): Either<Validation, EpisodeId> = EpisodeId(this)

fun Int.toEpisodeNumber(): Either<Validation, EpisodeNumber> = EpisodeNumber(this)

fun Episode.toApiEpisode(): ApiEpisode =
    ApiEpisode(
        id = id.toString(),
        number = number.toInt(),
        title = title.toString(),
        releaseDate = releaseDate,
        rating = rating.toInt(),
    )
