package com.saefulrdevs.mubeego.core.domain.usecase

import com.saefulrdevs.mubeego.core.domain.model.Playlist
import com.saefulrdevs.mubeego.core.domain.model.PlaylistItem
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.domain.repository.IPlaylistRepository
import kotlinx.coroutines.flow.Flow

interface PlaylistUseCase {
    fun createPlaylist(playlist: Playlist): Flow<Resource<Unit>>
    fun addItemToPlaylist(userId: String, playlistId: String, item: PlaylistItem): Flow<Resource<Unit>>
    fun removeItemFromPlaylist(userId: String, playlistId: String, itemId: Long): Flow<Resource<Unit>>
    fun getUserPlaylists(userId: String): Flow<Resource<List<Playlist>>>
//    fun getPublicPlaylists(): Flow<Resource<List<Playlist>>>
    fun getPlaylistDetails(userId: String, playlistId: String): Flow<Resource<Playlist>>
    fun updatePlaylistVisibility(userId: String, playlistId: String, isPublic: Boolean): Flow<Resource<Unit>>
}

class PlaylistInteractor(
    private val repository: IPlaylistRepository
) : PlaylistUseCase {
    
    override fun createPlaylist(playlist: Playlist): Flow<Resource<Unit>> =
        repository.createPlaylist(playlist)

    override fun addItemToPlaylist(userId: String, playlistId: String, item: PlaylistItem): Flow<Resource<Unit>> =
        repository.addItemToPlaylist(userId, playlistId, item)

    override fun removeItemFromPlaylist(userId: String, playlistId: String, itemId: Long): Flow<Resource<Unit>> =
        repository.removeItemFromPlaylist(userId, playlistId, itemId)

    override fun getUserPlaylists(userId: String): Flow<Resource<List<Playlist>>> =
        repository.getUserPlaylists(userId)

//    override fun getPublicPlaylists(): Flow<Resource<List<Playlist>>> =
//        repository.getPublicPlaylists()

    override fun getPlaylistDetails(userId: String, playlistId: String): Flow<Resource<Playlist>> =
        repository.getPlaylistDetails(userId, playlistId)

    override fun updatePlaylistVisibility(userId: String, playlistId: String, isPublic: Boolean): Flow<Resource<Unit>> =
        repository.updatePlaylistVisibility(userId, playlistId, isPublic)
}
