package dev.margeta.lab.graphql.spring.spi.persistence

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument.AFTER
import com.mongodb.reactivestreams.client.MongoDatabase
import dev.margeta.lab.graphql.spring.app.EpisodeRepository
import dev.margeta.lab.graphql.spring.domain.Episode
import dev.margeta.lab.graphql.spring.domain.EpisodeId
import dev.margeta.lab.graphql.spring.domain.Error
import dev.margeta.lab.graphql.spring.domain.Error.NotFound
import dev.margeta.lab.graphql.spring.domain.Error.Provider
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull

class EpisodeMongoRepository(database: MongoDatabase) : EpisodeRepository {
    private val collection = database.getCollection("episodes")

    override suspend fun list(): Either<Error, List<Episode>> = either {
        collection
            .find()
            .asFlow()
            .filterNotNull()
            .map { it.toEpisode().bind() }
            .toList()
    }

    override suspend fun get(id: EpisodeId): Either<Error, Episode> = either {
        val document = catchAndRaise {
            collection
                .find(eq(id.toString()))
                .awaitFirstOrNull()
        }
        ensure(document != null) { NotFound }
        document.toEpisode().bind()
    }

    private inline fun <A> Raise<Error>.catchAndRaise(block: () -> A): A =
        catch<RuntimeException, A>({ block.invoke() }) {
            raise(Provider(it))
        }

    override suspend fun create(episode: Episode): Either<Error, Episode> = either {
        catchAndRaise {
            collection
                .insertOne(episode.toDocument())
                .awaitFirstOrNull()
        }
        episode
    }

    override suspend fun save(episode: Episode): Either<Error, Episode> = either {
        val document = catchAndRaise {
            collection
                .findOneAndUpdate(eq(episode.id.toString()), episode.toUpdates(), updateOptions)
                .awaitFirstOrNull()
        }
        ensure(document != null) { NotFound }
        document.toEpisode().bind()
    }

    override suspend fun delete(id: EpisodeId): Either<Error, Episode> =
        either {
            val document = catchAndRaise {
                collection
                    .findOneAndDelete(eq(id.toString()))
                    .awaitFirstOrNull()
            }
            ensure(document != null) { NotFound }
            document.toEpisode().bind()
        }

    companion object {
        private val updateOptions =
            FindOneAndUpdateOptions()
                .upsert(false)
                .returnDocument(AFTER)
    }
}
