package com.saefulrdevs.mubeego.core.domain.model

data class Season(
    var seasonId: Int,
    var tvShowId: Int,
    var name: String = "",
    var overview: String = "",
    var airDate: String = "",
    var seasonNumber: Int = 0,
    var episodeCount: Int = 0,
    var posterPath: String = ""
)
