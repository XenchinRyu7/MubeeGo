package com.saefulrdevs.mubeego.core.data

import com.google.firebase.firestore.FirebaseFirestore
import com.saefulrdevs.mubeego.core.domain.model.Genre
import kotlinx.coroutines.tasks.await

class GenreRepository(private val firestore: FirebaseFirestore) {
    suspend fun getGenres(): List<Genre> {
        val snapshot = firestore.collection("genres").get().await()
        return snapshot.documents.mapNotNull { doc ->
            val name = doc.getString("name") ?: return@mapNotNull null
            Genre(id = doc.id, name = name)
        }
    }
}
