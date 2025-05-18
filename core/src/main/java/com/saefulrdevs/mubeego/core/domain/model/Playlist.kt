package com.saefulrdevs.mubeego.core.domain.model

import com.google.firebase.Timestamp

data class Playlist(
    val id: String = "",
    val name: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val isPublic: Boolean = false,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val items: List<PlaylistItem> = emptyList()
)

data class PlaylistItem(
    val itemId: Long = 0L,
    val itemType: MediaType = MediaType.MOVIE,
    val addedAt: Timestamp = Timestamp.now()
)

enum class MediaType {
    MOVIE, TV
}
