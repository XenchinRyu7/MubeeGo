package com.saefulrdevs.mubeego.core.domain.repository

import com.saefulrdevs.mubeego.core.domain.model.Playlist
import com.saefulrdevs.mubeego.core.domain.model.PlaylistItem
import com.saefulrdevs.mubeego.core.data.Resource
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun createPlaylist(playlist: Playlist): Flow<Resource<Unit>>
    fun addItemToPlaylist(playlistId: String, item: PlaylistItem): Flow<Resource<Unit>>
    fun removeItemFromPlaylist(playlistId: String, itemId: Long): Flow<Resource<Unit>>
    fun getUserPlaylists(userId: String): Flow<Resource<List<Playlist>>>
    fun getPublicPlaylists(): Flow<Resource<List<Playlist>>>
    fun getPlaylistDetails(playlistId: String): Flow<Resource<Playlist>>
    fun updatePlaylistVisibility(playlistId: String, isPublic: Boolean): Flow<Resource<Unit>>
}
