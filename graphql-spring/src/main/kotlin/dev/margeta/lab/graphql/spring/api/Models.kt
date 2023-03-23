package dev.margeta.lab.graphql.spring.api

import java.time.LocalDate

data class Director(
    val id: String,
    val firstName: String,
    val lastName: String,
    val episodes: List<Episode> = emptyList(),
)

data class Hero(
    val id: String,
    val name: String,
    val heightMeters: Double,
    val lightSaber: LightSaber,
    val order: Order,
    val rating: Int,
    val episodes: List<Episode> = emptyList(),
)

enum class LightSaber {
    AMETHYST,
    BLACK,
    BLUE,
    CYAN,
    GREEN,
    INDIGO,
    MAGENTA,
    ORANGE,
    RED,
    WHITE,
    YELLOW,
}

enum class Order {
    JEDI_ORDER,
    SITH,
}

data class Episode(
    val id: String,
    val number: Int,
    val title: String,
    val director: Director,
    val releaseDate: LocalDate,
    val rating: Int,
    val heroes: List<Hero> = emptyList(),
)

data class CreateEpisodeInput(
    val number: Int,
    val title: String,
    val directorId: String,
    val releaseDate: LocalDate,
)
