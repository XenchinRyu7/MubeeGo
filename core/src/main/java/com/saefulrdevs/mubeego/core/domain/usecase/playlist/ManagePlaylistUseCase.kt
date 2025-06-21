package com.saefulrdevs.mubeego.core.domain.usecase.playlist

import com.saefulrdevs.mubeego.core.domain.model.PlaylistItem
import com.saefulrdevs.mubeego.core.domain.repository.PlaylistRepository
import com.saefulrdevs.mubeego.core.data.Resource
import kotlinx.coroutines.flow.Flow

class ManagePlaylistUseCase(private val repository: PlaylistRepository) {
    fun addItem(playlistId: String, item: PlaylistItem): Flow<Resource<Unit>> {
        return repository.addItemToPlaylist(playlistId, item)
    }

    fun removeItem(playlistId: String, itemId: Long): Flow<Resource<Unit>> {
        return repository.removeItemFromPlaylist(playlistId, itemId)
    }

    fun updateVisibility(playlistId: String, isPublic: Boolean): Flow<Resource<Unit>> {
        return repository.updatePlaylistVisibility(playlistId, isPublic)
    }
}
