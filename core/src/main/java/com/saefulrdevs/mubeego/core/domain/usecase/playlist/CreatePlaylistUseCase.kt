package com.saefulrdevs.mubeego.core.domain.usecase.playlist

import com.saefulrdevs.mubeego.core.domain.model.Playlist
import com.saefulrdevs.mubeego.core.domain.repository.PlaylistRepository
import com.saefulrdevs.mubeego.core.data.Resource
import kotlinx.coroutines.flow.Flow

class CreatePlaylistUseCase(private val repository: PlaylistRepository) {
    operator fun invoke(playlist: Playlist): Flow<Resource<Unit>> {
        return repository.createPlaylist(playlist)
    }
}
