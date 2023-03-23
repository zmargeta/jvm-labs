package dev.margeta.lab.graphql.spring.spi.persistence

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.either
import com.mongodb.client.model.Updates
import dev.margeta.lab.graphql.spring.domain.Director
import dev.margeta.lab.graphql.spring.domain.Episode
import dev.margeta.lab.graphql.spring.domain.Error.Provider
import dev.margeta.lab.graphql.spring.domain.HeroId
import dev.margeta.lab.graphql.spring.domain.Validation
import org.bson.Document
import org.bson.conversions.Bson
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

private const val DIRECTOR_ID_KEY = "_id";
private const val DIRECTOR_FIRST_NAME_KEY = "firstName"
private const val DIRECTOR_LAST_NAME_KEY = "lastName"

fun Document.toDirector(): Either<Provider, Director> = either {
    val director =
        Director(
            id = getString(DIRECTOR_ID_KEY)!!,
            firstName = getString(DIRECTOR_FIRST_NAME_KEY)!!,
            lastName = getString(DIRECTOR_LAST_NAME_KEY)!!,
        )
    director.mapLeft { Provider(InconsistentDataException()) }.bind()
}

private const val EPISODE_ID_KEY = "_id"
private const val EPISODE_NUMBER_KEY = "number"
private const val EPISODE_TITLE_KEY = "title"
private const val EPISODE_DIRECTOR_ID_KEY = "directorId"
private const val EPISODE_RELEASE_DATE_KEY = "releaseDate"
private const val EPISODE_RATING_KEY = "rating"
private const val EPISODE_HERO_IDS_KEY = "heroIds"

fun Document.toEpisode(): Either<NonEmptyList<Validation>, Episode> =
    Episode(
        id = getString(EPISODE_ID_KEY)!!,
        number = getInteger(EPISODE_NUMBER_KEY)!!,
        title = getString(EPISODE_TITLE_KEY)!!,
        directorId = getString(EPISODE_DIRECTOR_ID_KEY),
        releaseDate = getDate(EPISODE_RELEASE_DATE_KEY)!!.toLocalDate(),
        rating = getInteger(EPISODE_RATING_KEY)!!,
        heroIds = getList(EPISODE_HERO_IDS_KEY, String::class.java)!!,
    )

private fun Date.toLocalDate(): LocalDate = toInstant().atZone(ZoneId.of("Z")).toLocalDate()

fun Director.toDocument(): Document = Document().apply {
    put(DIRECTOR_ID_KEY, id.toString())
    put(DIRECTOR_FIRST_NAME_KEY, firstName.toString())
    put(DIRECTOR_LAST_NAME_KEY, lastName.toString())
}

fun Episode.toDocument(): Document = Document().apply {
    put(EPISODE_ID_KEY, id.toString())
    put(EPISODE_NUMBER_KEY, number.toInt())
    put(EPISODE_TITLE_KEY, title.toString())
    put(EPISODE_DIRECTOR_ID_KEY, directorId.getOrNull()?.toString())
    put(EPISODE_RELEASE_DATE_KEY, releaseDate.toDate())
    put(EPISODE_RATING_KEY, rating.toInt())
    put(EPISODE_HERO_IDS_KEY, heroIds.map(HeroId::toString))
}

private fun LocalDate.toDate(): Date = Date.from(atStartOfDay().atZone(ZoneId.of("Z")).toInstant())

fun Director.toUpdates(): List<Bson> = buildList {
    add(Updates.set(DIRECTOR_FIRST_NAME_KEY, firstName.toString()))
    add(Updates.set(DIRECTOR_LAST_NAME_KEY, lastName.toString()))
}

fun Episode.toUpdates(): List<Bson> = buildList {
    add(Updates.set(EPISODE_NUMBER_KEY, number.toInt()))
    add(Updates.set(EPISODE_TITLE_KEY, title.toString()))
    add(Updates.set(EPISODE_DIRECTOR_ID_KEY, directorId.getOrNull()?.toString()))
    add(Updates.set(EPISODE_RELEASE_DATE_KEY, releaseDate.toDate()))
    add(Updates.set(EPISODE_RATING_KEY, rating.toInt()))
    add(Updates.set(EPISODE_HERO_IDS_KEY, heroIds.map(HeroId::toString)))
}
