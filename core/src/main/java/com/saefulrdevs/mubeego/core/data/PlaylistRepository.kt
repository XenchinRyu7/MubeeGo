package com.saefulrdevs.mubeego.core.data

import com.saefulrdevs.mubeego.core.data.source.firebase.PlaylistFirestoreDataSource
import com.saefulrdevs.mubeego.core.domain.model.Playlist
import com.saefulrdevs.mubeego.core.domain.model.PlaylistItem
import com.saefulrdevs.mubeego.core.domain.repository.IPlaylistRepository
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(
    private val firestoreDataSource: PlaylistFirestoreDataSource
) : IPlaylistRepository {

    override fun createPlaylist(playlist: Playlist): Flow<Resource<Unit>> {
        return firestoreDataSource.createPlaylist(playlist)
    }

    override fun addItemToPlaylist(userId: String, playlistId: String, item: PlaylistItem): Flow<Resource<Unit>> {
        return firestoreDataSource.addItemToPlaylist(userId, playlistId, item)
    }

    override fun removeItemFromPlaylist(userId: String, playlistId: String, itemId: Long): Flow<Resource<Unit>> {
        return firestoreDataSource.removeItemFromPlaylist(userId, playlistId, itemId)
    }

    override fun getUserPlaylists(userId: String): Flow<Resource<List<Playlist>>> {
        return firestoreDataSource.getUserPlaylists(userId)
    }

//    override fun getPublicPlaylists(): Flow<Resource<List<Playlist>>> {
//        return firestoreDataSource.getPublicPlaylists()
//    }

    override fun getPlaylistDetails(userId: String, playlistId: String): Flow<Resource<Playlist>> {
        return firestoreDataSource.getPlaylistDetails(userId, playlistId)
    }

    override fun updatePlaylistVisibility(userId: String, playlistId: String, isPublic: Boolean): Flow<Resource<Unit>> {
        return firestoreDataSource.updatePlaylistVisibility(userId, playlistId, isPublic)
    }
}
