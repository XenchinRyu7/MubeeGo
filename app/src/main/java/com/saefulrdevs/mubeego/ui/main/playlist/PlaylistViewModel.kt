package com.saefulrdevs.mubeego.ui.main.playlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saefulrdevs.mubeego.core.domain.model.Playlist
import com.saefulrdevs.mubeego.core.domain.model.PlaylistItem
import com.saefulrdevs.mubeego.core.domain.usecase.PlaylistUseCase
import com.saefulrdevs.mubeego.core.data.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistUseCase: PlaylistUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "PlaylistViewModel"
    }

    private val _userPlaylists = MutableStateFlow<Resource<List<Playlist>>>(Resource.Loading())
    val userPlaylists: StateFlow<Resource<List<Playlist>>> = _userPlaylists

    private val _publicPlaylists = MutableStateFlow<Resource<List<Playlist>>>(Resource.Loading())
    val publicPlaylists: StateFlow<Resource<List<Playlist>>> = _publicPlaylists

    fun createPlaylist(playlist: Playlist) {
        Log.d(TAG, "Creating playlist: $playlist")
        viewModelScope.launch {
            playlistUseCase.createPlaylist(playlist).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.d(TAG, "Playlist created successfully")
                        // Refresh playlists after creation
                        getUserPlaylists(playlist.ownerId)
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "Error creating playlist: ${result.message}")
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "Creating playlist in progress...")
                    }
                }
            }
        }
    }

    fun addItemToPlaylist(userId: String, playlistId: String, item: PlaylistItem) {
        Log.d(TAG, "Adding item to playlist: userId=$userId, playlistId=$playlistId, item=$item")
        viewModelScope.launch {
            playlistUseCase.addItemToPlaylist(userId, playlistId, item).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.d(TAG, "Item added successfully")
                        // Refresh playlist details
                        getPlaylistDetails(userId, playlistId)
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "Error adding item: ${result.message}")
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "Adding item in progress...")
                    }
                }
            }
        }
    }

    fun removeItemFromPlaylist(userId: String, playlistId: String, itemId: Long) {
        Log.d(TAG, "Removing item: userId=$userId, playlistId=$playlistId, itemId=$itemId")
        viewModelScope.launch {
            playlistUseCase.removeItemFromPlaylist(userId, playlistId, itemId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.d(TAG, "Item removed successfully")
                        // Refresh playlist details
                        getPlaylistDetails(userId, playlistId)
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "Error removing item: ${result.message}")
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "Removing item in progress...")
                    }
                }
            }
        }
    }

    fun getUserPlaylists(userId: String) {
        Log.d(TAG, "Getting playlists for user: $userId")
        viewModelScope.launch {
            playlistUseCase.getUserPlaylists(userId).collect { result ->
                Log.d(TAG, "User playlists result: $result")
                _userPlaylists.value = result
            }
        }
    }

    fun getPublicPlaylists() {
        Log.d(TAG, "Getting public playlists")
        viewModelScope.launch {
            playlistUseCase.getPublicPlaylists().collect { result ->
                Log.d(TAG, "Public playlists result: $result")
                _publicPlaylists.value = result
            }
        }
    }

    private fun getPlaylistDetails(userId: String, playlistId: String) {
        Log.d(TAG, "Getting playlist details: userId=$userId, playlistId=$playlistId")
        viewModelScope.launch {
            playlistUseCase.getPlaylistDetails(userId, playlistId)
        }
    }

    fun updatePlaylistVisibility(userId: String, playlistId: String, isPublic: Boolean) {
        Log.d(TAG, "Updating playlist visibility: userId=$userId, playlistId=$playlistId, isPublic=$isPublic")
        viewModelScope.launch {
            playlistUseCase.updatePlaylistVisibility(userId, playlistId, isPublic).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.d(TAG, "Visibility updated successfully")
                        // Refresh playlists after update
                        getUserPlaylists(userId)
                        getPublicPlaylists()
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "Error updating visibility: ${result.message}")
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "Updating visibility in progress...")
                    }
                }
            }
        }
    }
}
