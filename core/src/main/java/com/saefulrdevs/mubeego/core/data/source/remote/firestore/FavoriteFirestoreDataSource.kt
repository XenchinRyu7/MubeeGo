package com.saefulrdevs.mubeego.core.data.source.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.model.TvShow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FavoriteFirestoreDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun getFavoriteMovies(): List<Movie> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("favorites_movies")
            .get().await()
        return snapshot.documents.mapNotNull { doc ->
            val id = doc.id.toIntOrNull() ?: return@mapNotNull null
            Movie(movieId = id) // Lengkapi mapping jika ada field lain
        }
    }

    suspend fun getFavoriteTvShows(): List<TvShow> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("favorites_tv")
            .get().await()
        return snapshot.documents.mapNotNull { doc ->
            val id = doc.id.toIntOrNull() ?: return@mapNotNull null
            TvShow(tvShowId = id) // Lengkapi mapping jika ada field lain
        }
    }

    suspend fun isMovieFavorited(movieId: Int): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        val doc = firestore.collection("users")
            .document(userId)
            .collection("favorites_movies")
            .document(movieId.toString())
            .get().await()
        return doc.exists() && (doc.getBoolean("favorited") == true)
    }

    suspend fun isTvShowFavorited(tvShowId: Int): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        val doc = firestore.collection("users")
            .document(userId)
            .collection("favorites_tv")
            .document(tvShowId.toString())
            .get().await()
        return doc.exists() && (doc.getBoolean("favorited") == true)
    }

    fun observeFavoriteMovieIds(): Flow<List<Int>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val registration = firestore.collection("users")
            .document(userId)
            .collection("favorites_movies")
            .addSnapshotListener { snapshot, _ ->
                val ids = snapshot?.documents?.mapNotNull { it.id.toIntOrNull() } ?: emptyList()
                trySend(ids)
            }
        awaitClose { registration.remove() }
    }

    fun observeFavoriteTvShowIds(): Flow<List<Int>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val registration = firestore.collection("users")
            .document(userId)
            .collection("favorites_tv")
            .addSnapshotListener { snapshot, _ ->
                val ids = snapshot?.documents?.mapNotNull { it.id.toIntOrNull() } ?: emptyList()
                trySend(ids)
            }
        awaitClose { registration.remove() }
    }
}
