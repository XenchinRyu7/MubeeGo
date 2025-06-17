package com.saefulrdevs.mubeego.core.domain.repository

import com.saefulrdevs.mubeego.core.domain.model.Playlist
import com.saefulrdevs.mubeego.core.domain.model.PlaylistItem
import com.saefulrdevs.mubeego.core.data.Resource
import kotlinx.coroutines.flow.Flow

interface IPlaylistRepository {
    fun createPlaylist(playlist: Playlist): Flow<Resource<Unit>>
    fun addItemToPlaylist(userId: String, playlistId: String, item: PlaylistItem): Flow<Resource<Unit>>
    fun removeItemFromPlaylist(userId: String, playlistId: String, itemId: Long): Flow<Resource<Unit>>
    fun getUserPlaylists(userId: String): Flow<Resource<List<Playlist>>>
//    fun getPublicPlaylists(): Flow<Resource<List<Playlist>>>
    fun getPlaylistDetails(userId: String, playlistId: String): Flow<Resource<Playlist>>
    fun updatePlaylistVisibility(userId: String, playlistId: String, isPublic: Boolean): Flow<Resource<Unit>>
    fun updatePlaylistData(userId: String, playlistId: String, name: String, notes: String): Flow<Resource<Unit>>
}
