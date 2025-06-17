package com.saefulrdevs.mubeego.ui.main.playlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saefulrdevs.mubeego.core.domain.model.Playlist
import com.saefulrdevs.mubeego.core.domain.model.PlaylistItem
import com.saefulrdevs.mubeego.core.domain.usecase.PlaylistUseCase
import com.saefulrdevs.mubeego.core.domain.usecase.TmdbUseCase
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.domain.model.MediaType
import com.saefulrdevs.mubeego.core.domain.model.SearchItem
import com.saefulrdevs.mubeego.core.util.DataMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistViewModel(
    private val playlistUseCase: PlaylistUseCase,
    private val tmdbUseCase: TmdbUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "PlaylistViewModel"
    }

    private val _userPlaylists = MutableStateFlow<Resource<List<Playlist>>>(Resource.Loading())
    val userPlaylists: StateFlow<Resource<List<Playlist>>> = _userPlaylists

    private val _publicPlaylists = MutableStateFlow<Resource<List<Playlist>>>(Resource.Loading())
    val publicPlaylists: StateFlow<Resource<List<Playlist>>> = _publicPlaylists

    private val _playlistDetail = MutableStateFlow<Resource<Playlist>>(Resource.Loading())
    val playlistDetail: StateFlow<Resource<Playlist>> = _playlistDetail

    private val _playlistItems = MutableStateFlow<Resource<List<PlaylistItem>>>(Resource.Loading())
    val playlistItems: StateFlow<Resource<List<PlaylistItem>>> = _playlistItems

    private val _playlistSearchItems = MutableStateFlow<Resource<List<SearchItem>>>(Resource.Loading())
    val playlistSearchItems: StateFlow<Resource<List<SearchItem>>> = _playlistSearchItems

    init {
        viewModelScope.launch {
            playlistItems.collect { result ->
                Log.d(TAG, "playlistItems changed: $result")
                if (result is Resource.Success) {
                    fetchAndMapPlaylistItems(result.data ?: emptyList())
                } else if (result is Resource.Error) {
                    _playlistSearchItems.value = Resource.Error(result.message ?: "Failed to load playlist items")
                } else if (result is Resource.Loading) {
                    _playlistSearchItems.value = Resource.Loading()
                }
            }
        }
    }

    private suspend fun fetchAndMapPlaylistItems(items: List<PlaylistItem>) {
        Log.d(TAG, "fetchAndMapPlaylistItems called with ${items.size} items")
        items.forEachIndexed { idx, item ->
            Log.d(TAG, "[fetchAndMapPlaylistItems] item[$idx]: id=${item.itemId}, type=${item.itemType}, raw=$item")
        }
        _playlistSearchItems.value = Resource.Loading()
        val searchItems = withContext(Dispatchers.IO) {
            items.mapNotNull { item ->
                try {
                    Log.d(TAG, "Fetching detail for item: id=${item.itemId}, type=${item.itemType}, raw=$item")
                    when (item.itemType) {
                        MediaType.MOVIE -> {
                            val movieDetail = tmdbUseCase.getMovieDetailRemote(item.itemId.toString())
                            Log.d(TAG, "Fetched movieDetail for id=${item.itemId}: $movieDetail")
                            movieDetail?.let {
                                val movie = DataMapper.run { it.toEntity().toDomain() }
                                val searchItem = DataMapper.movieToSearchItem(movie)
                                Log.d(TAG, "Mapped movie to SearchItem: $searchItem")
                                searchItem
                            }
                        }
                        MediaType.TV -> {
                            val tvDetail = tmdbUseCase.getTvShowDetailRemote(item.itemId.toString())
                            Log.d(TAG, "Fetched tvDetail for id=${item.itemId}: $tvDetail")
                            tvDetail?.let {
                                val tvShow = DataMapper.run { it.toEntity().toDomain() }
                                val searchItem = DataMapper.tvShowToSearchItem(tvShow)
                                Log.d(TAG, "Mapped tvShow to SearchItem: $searchItem")
                                searchItem
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching detail for item: $item", e)
                    null
                }
            }
        }
        Log.d(TAG, "Mapped searchItems: $searchItems")
        _playlistSearchItems.value = Resource.Success(searchItems)
    }

    fun createPlaylist(playlist: Playlist) {
        Log.d(TAG, "Creating playlist: $playlist")
        viewModelScope.launch {
            playlistUseCase.createPlaylist(playlist).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.d(TAG, "Playlist created successfully")
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

//    fun getPublicPlaylists() {
//        Log.d(TAG, "Getting public playlists")
//        viewModelScope.launch {
//            playlistUseCase.getPublicPlaylists().collect { result ->
//                Log.d(TAG, "Public playlists result: $result")
//                _publicPlaylists.value = result
//            }
//        }
//    }

    fun getPlaylistDetails(userId: String, playlistId: String) {
        Log.d(TAG, "ViewModel.getPlaylistDetails called with userId=$userId, playlistId=$playlistId")
        viewModelScope.launch {
            playlistUseCase.getPlaylistDetails(userId, playlistId).collect { result ->
                _playlistDetail.value = result
                if (result is Resource.Success && result.data != null) {
                    // Fetch items after getting playlist detail
                    getPlaylistItems(userId, playlistId)
                }
            }
        }
    }

    private fun getPlaylistItems(userId: String, playlistId: String) {
        Log.d(TAG, "Getting playlist items: userId=$userId, playlistId=$playlistId")
        val playlist = _playlistDetail.value
        if (playlist is Resource.Success && playlist.data != null) {
            _playlistItems.value = Resource.Success(playlist.data!!.items)
        } else {
            _playlistItems.value = Resource.Error("Playlist items not available")
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
                        // Tambahan: refresh detail playlist agar isPublic update di UI
                        getPlaylistDetails(userId, playlistId)
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

    fun updatePlaylistData(userId: String, playlistId: String, name: String, notes: String, onResult: ((Resource<Unit>) -> Unit)? = null) {
        Log.d(TAG, "Updating playlist data: userId=$userId, playlistId=$playlistId, name=$name, notes=$notes")
        viewModelScope.launch {
            playlistUseCase.updatePlaylistData(userId, playlistId, name, notes).collect { result ->
                if (result is Resource.Success) {
                    getPlaylistDetails(userId, playlistId)
                }
                onResult?.invoke(result)
            }
        }
    }
}
