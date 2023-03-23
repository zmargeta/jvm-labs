package dev.margeta.lab.graphql.spring.api

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono

@Controller
class HeroController {
    @QueryMapping
    fun hero(@Argument id: String): Mono<Hero> = TODO("Not yet implemented")

    @QueryMapping
    fun heroes(): Mono<List<Hero>> = TODO("Not yet implemented")
}
