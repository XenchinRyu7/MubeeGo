package com.saefulrdevs.mubeego.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class GenreListResponse(
    @SerializedName("genres")
    val genres: List<GenreResponse>
)

data class GenreResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)
