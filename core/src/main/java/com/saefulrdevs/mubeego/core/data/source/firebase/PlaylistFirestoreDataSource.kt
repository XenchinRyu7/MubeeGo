package com.saefulrdevs.mubeego.core.data.source.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.saefulrdevs.mubeego.core.domain.model.Playlist
import com.saefulrdevs.mubeego.core.domain.model.PlaylistItem
import com.saefulrdevs.mubeego.core.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp

class PlaylistFirestoreDataSource(private val firestore: FirebaseFirestore) {
    
    companion object {
        private const val TAG = "PlaylistFirestore"
        private const val USERS_COLLECTION = "users"
        private const val PLAYLISTS_COLLECTION = "playlists"
        private const val ITEMS_COLLECTION = "items"
    }

    fun createPlaylist(playlist: Playlist): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            Log.d(TAG, "Creating playlist: $playlist")
            
            val playlistRef = firestore.collection(USERS_COLLECTION)
                .document(playlist.ownerId)
                .collection(PLAYLISTS_COLLECTION)
                .document()

            val playlistData = hashMapOf(
                "id" to playlistRef.id,
                "name" to playlist.name,
                "notes" to playlist.notes,
                "ownerId" to playlist.ownerId,
                "ownerName" to playlist.ownerName,
                "isPublic" to playlist.isPublic,
                "createdAt" to Timestamp.now(),
                "updatedAt" to Timestamp.now()
            )
            
            Log.d(TAG, "Saving playlist data: $playlistData")
            playlistRef.set(playlistData).await()
            Log.d(TAG, "Playlist created successfully with ID: ${playlistRef.id}")
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            Log.e(TAG, "Error creating playlist", e)
            emit(Resource.Error(e.message ?: "Failed to create playlist"))
        }
    }

    fun addItemToPlaylist(userId: String, playlistId: String, item: PlaylistItem): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            Log.d(TAG, "Adding item to playlist: userId=$userId, playlistId=$playlistId, item=$item")
            
            val itemRef = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(PLAYLISTS_COLLECTION)
                .document(playlistId)
                .collection(ITEMS_COLLECTION)
                .document()

            val itemData = hashMapOf(
                "itemId" to item.itemId,
                "itemType" to item.itemType.name,
                "addedAt" to Timestamp.now()
            )

            Log.d(TAG, "Saving item data: $itemData")
            itemRef.set(itemData).await()
            Log.d(TAG, "Item added successfully with ID: ${itemRef.id}")
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            Log.e(TAG, "Error adding item to playlist", e)
            emit(Resource.Error(e.message ?: "Failed to add item to playlist"))
        }
    }

    fun getUserPlaylists(userId: String): Flow<Resource<List<Playlist>>> = flow {
        try {
            emit(Resource.Loading())
            Log.d(TAG, "Getting playlists for user: $userId")
            
            val playlists = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(PLAYLISTS_COLLECTION)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .documents
                .mapNotNull { doc ->
                    val data = doc.data
                    val isPublic = when (val value = data?.get("isPublic")) {
                        is Boolean -> value
                        is String -> value.toBoolean()
                        is Number -> value.toInt() != 0
                        else -> false
                    }
                    val playlist = doc.toObject(Playlist::class.java)?.copy(isPublic = isPublic)
                    Log.d(TAG, "FETCHED PLAYLIST: ${playlist?.name} isPublic=${playlist?.isPublic} raw=${doc.data}")
                    playlist
                }
            
            Log.d(TAG, "Retrieved ${playlists.size} playlists for user")
            emit(Resource.Success(playlists))
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user playlists", e)
            emit(Resource.Error(e.message ?: "Failed to get user playlists"))
        }
    }

//    fun getPublicPlaylists(): Flow<Resource<List<Playlist>>> = flow {
//        try {
//            emit(Resource.Loading())
//            Log.d(TAG, "Getting public playlists")
//
//            val publicPlaylists = mutableListOf<Playlist>()
//
//            val usersSnapshot = firestore.collection(USERS_COLLECTION)
//                .get()
//                .await()
//
//            Log.d(TAG, "Found ${usersSnapshot.size()} users")
//
//            for (userDoc in usersSnapshot.documents) {
//                val userPlaylists = userDoc.reference
//                    .collection(PLAYLISTS_COLLECTION)
//                    .whereEqualTo("isPublic", true)
//                    .orderBy("updatedAt", Query.Direction.DESCENDING)
//                    .get()
//                    .await()
//                    .documents
//                    .mapNotNull { doc ->
//                        doc.toObject(Playlist::class.java)
//                    }
//                Log.d(TAG, "Found ${userPlaylists.size} public playlists for user ${userDoc.id}")
//                publicPlaylists.addAll(userPlaylists)
//            }
//
//            publicPlaylists.sortByDescending { it.updatedAt.seconds }
//            Log.d(TAG, "Retrieved ${publicPlaylists.size} total public playlists")
//            emit(Resource.Success(publicPlaylists))
//        } catch (e: Exception) {
//            Log.e(TAG, "Error getting public playlists", e)
//            emit(Resource.Error(e.message ?: "Failed to get public playlists"))
//        }
//    }

    fun updatePlaylistVisibility(userId: String, playlistId: String, isPublic: Boolean): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            Log.d(TAG, "Updating playlist visibility: userId=$userId, playlistId=$playlistId, isPublic=$isPublic")
            
            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(PLAYLISTS_COLLECTION)
                .document(playlistId)
                .update(
                    mapOf(
                        "isPublic" to isPublic,
                        "updatedAt" to Timestamp.now()
                    )
                )
                .await()
            
            Log.d(TAG, "Playlist visibility updated successfully")
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            Log.e(TAG, "Error updating playlist visibility", e)
            emit(Resource.Error(e.message ?: "Failed to update playlist visibility"))
        }
    }

    fun getPlaylistDetails(userId: String, playlistId: String): Flow<Resource<Playlist>> = flow {
        try {
            emit(Resource.Loading())
            Log.d(TAG, "Getting playlist details: userId=$userId, playlistId=$playlistId")
            
            val playlist = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(PLAYLISTS_COLLECTION)
                .document(playlistId)
                .get()
                .await()
                .toObject(Playlist::class.java)
                ?: throw Exception("Playlist not found")

            Log.d(TAG, "Retrieved playlist: $playlist")

            val items = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(PLAYLISTS_COLLECTION)
                .document(playlistId)
                .collection(ITEMS_COLLECTION)
                .orderBy("addedAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .documents
                .mapNotNull { doc ->
                    doc.toObject(PlaylistItem::class.java)
                }

            Log.d(TAG, "Retrieved ${items.size} items for playlist")
            emit(Resource.Success(playlist.copy(items = items)))
        } catch (e: Exception) {
            Log.e(TAG, "Error getting playlist details", e)
            emit(Resource.Error(e.message ?: "Failed to get playlist details"))
        }
    }

    fun removeItemFromPlaylist(userId: String, playlistId: String, itemId: Long): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            Log.d(TAG, "Removing item: userId=$userId, playlistId=$playlistId, itemId=$itemId")
            
            val itemQuery = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(PLAYLISTS_COLLECTION)
                .document(playlistId)
                .collection(ITEMS_COLLECTION)
                .whereEqualTo("itemId", itemId)
                .get()
                .await()

            if (!itemQuery.isEmpty) {
                itemQuery.documents[0].reference.delete().await()
                Log.d(TAG, "Item removed successfully")
            } else {
                Log.w(TAG, "Item not found in playlist")
            }
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            Log.e(TAG, "Error removing item from playlist", e)
            emit(Resource.Error(e.message ?: "Failed to remove item from playlist"))
        }
    }
}
