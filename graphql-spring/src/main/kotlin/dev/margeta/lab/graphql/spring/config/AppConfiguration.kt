package dev.margeta.lab.graphql.spring.config

import com.github.f4b6a3.ulid.UlidCreator
import com.mongodb.reactivestreams.client.MongoDatabase
import dev.margeta.lab.graphql.spring.app.EpisodeRepository
import dev.margeta.lab.graphql.spring.app.EpisodeService
import dev.margeta.lab.graphql.spring.app.IdentityService
import dev.margeta.lab.graphql.spring.spi.persistence.EpisodeMongoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class AppConfiguration {
    @Bean
    fun episodeRepository(database: MongoDatabase): EpisodeRepository = EpisodeMongoRepository(database)

    @Bean
    fun identityService(): IdentityService = IdentityService {
        val id = CoroutineScope(Dispatchers.IO).async {
            UlidCreator.getMonotonicUlid().toString()
        }
        id.await()
    }

    @Bean
    fun episodeService(episodeRepository: EpisodeRepository, identityService: IdentityService) =
        EpisodeService(episodeRepository, identityService)
}
