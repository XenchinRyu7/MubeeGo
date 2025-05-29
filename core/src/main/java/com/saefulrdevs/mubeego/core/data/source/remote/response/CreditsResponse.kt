package com.saefulrdevs.mubeego.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

// Response for /movie/{movie_id}/credits

data class CreditsResponse(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("cast")
    val cast: List<CastItem>?
)

data class CastItem(
    @SerializedName("cast_id")
    val castId: Int?,
    @SerializedName("character")
    val character: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("profile_path")
    val profilePath: String?,
    @SerializedName("order")
    val order: Int?
)
