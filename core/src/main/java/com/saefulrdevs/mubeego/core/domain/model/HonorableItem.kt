package com.saefulrdevs.mubeego.core.domain.model

data class HonorableItem(
    val id: String = "",
    val title: String = "",
    val originalTitle: String = "",
    val overview: String = "",
    val releaseDate: String = "",
    val type: String = "",
    val genreIds: List<String> = emptyList(),
    val originalLanguage: String = ""
)
