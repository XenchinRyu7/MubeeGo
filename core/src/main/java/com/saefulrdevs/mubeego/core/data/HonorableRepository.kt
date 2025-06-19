package com.saefulrdevs.mubeego.core.data

import com.google.firebase.firestore.FirebaseFirestore
import com.saefulrdevs.mubeego.core.domain.model.HonorableItem
import com.saefulrdevs.mubeego.core.domain.repository.IHonorableRepository
import kotlinx.coroutines.tasks.await

class HonorableRepository(private val firestore: FirebaseFirestore) : IHonorableRepository {
    override suspend fun getHonorableMentions(): List<HonorableItem> {
        val snapshot = firestore.collection("movies").get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(HonorableItem::class.java)?.copy(id = doc.id)
        }
    }
}
