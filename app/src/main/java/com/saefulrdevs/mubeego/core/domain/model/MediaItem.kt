package com.saefulrdevs.mubeego.core.domain.model

data class MediaItem(
    val id: Int = 0,
    val movieId: Int = 0,
    val tvShowId: Int = 0,
    val title: String? = null,
    val name: String? = null,
    val overview: String = "",
    val posterPath: String = "",
    val voteAverage: Double = 0.0,
    val mediaType: String = "", // "movie" or "tv"
    val genres: List<String> = emptyList(),
    val runtime: Int = 0,
    val originalLanguage: String = "",
    val firstAirDate: String = "",
    val favorited: Boolean = false
)
