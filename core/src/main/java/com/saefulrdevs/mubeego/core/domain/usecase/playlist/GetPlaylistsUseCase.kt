package com.saefulrdevs.mubeego.core.domain.usecase.playlist

import com.saefulrdevs.mubeego.core.domain.model.Playlist
import com.saefulrdevs.mubeego.core.domain.repository.PlaylistRepository
import com.saefulrdevs.mubeego.core.data.Resource
import kotlinx.coroutines.flow.Flow

class GetPlaylistsUseCase(private val repository: PlaylistRepository) {
    fun getUserPlaylists(userId: String): Flow<Resource<List<Playlist>>> {
        return repository.getUserPlaylists(userId)
    }

    fun getPublicPlaylists(): Flow<Resource<List<Playlist>>> {
        return repository.getPublicPlaylists()
    }

    fun getPlaylistDetails(playlistId: String): Flow<Resource<Playlist>> {
        return repository.getPlaylistDetails(playlistId)
    }
}
