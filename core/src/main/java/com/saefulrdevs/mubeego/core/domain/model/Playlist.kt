package com.saefulrdevs.mubeego.core.domain.model

import com.google.firebase.Timestamp

data class Playlist(
    val id: String = "",
    val name: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val isPublic: Boolean = false,
    val notes: String = "",
    val rating: Double = 0.0,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val items: List<PlaylistItem> = emptyList()
)

data class PlaylistItem(
    val id: Long = 0L,
    val type: String = "movie",
    val addedAt: Timestamp = Timestamp.now()
) {
    val itemId: Long get() = id
    val itemType: MediaType get() = if (type.equals("tv", true)) MediaType.TV else MediaType.MOVIE
}

enum class MediaType {
    MOVIE, TV
}
