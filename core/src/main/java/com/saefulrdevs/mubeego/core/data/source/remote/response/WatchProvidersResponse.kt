package com.saefulrdevs.mubeego.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

// TMDb /movie/{movie_id}/watch/providers response

data class WatchProvidersResponse(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("results")
    val results: Map<String, ProviderRegion>?
)

data class ProviderRegion(
    @SerializedName("link")
    val link: String?,
    @SerializedName("flatrate")
    val flatrate: List<ProviderItem>?,
    @SerializedName("rent")
    val rent: List<ProviderItem>?,
    @SerializedName("buy")
    val buy: List<ProviderItem>?
)

data class ProviderItem(
    @SerializedName("provider_id")
    val providerId: Int?,
    @SerializedName("provider_name")
    val providerName: String?,
    @SerializedName("logo_path")
    val logoPath: String?
)
