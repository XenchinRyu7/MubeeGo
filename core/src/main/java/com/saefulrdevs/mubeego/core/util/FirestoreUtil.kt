package com.saefulrdevs.mubeego.core.util

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.saefulrdevs.mubeego.core.domain.model.UserData
import kotlinx.coroutines.tasks.await

suspend fun fetchUserDataFromFirestore(uid: String): UserData? {
    return try {
        val doc = FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get(Source.SERVER)
            .await()
        Log.d("FirestoreUtil", "Fetched doc for uid=$uid: ${doc.data}")
        if (doc.exists()) {
            val user = UserData(
                uid = doc.getString("uid") ?: "",
                fullname = doc.getString("fullname") ?: "",
                email = doc.getString("email") ?: "",
                isPremium = doc.getBoolean("isPremium") == true,
                createdAt = doc.getTimestamp("createdAt")?.toDate()?.time
            )
            Log.d("FirestoreUtil", "Parsed user: $user")
            user
        } else null
    } catch (e: Exception) {
        Log.e("FirestoreUtil", "Error fetching user: $e")
        null
    }
}

suspend fun updateUserFullnameInFirestore(uid: String, newFullname: String): Boolean {
    return try {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .update("fullname", newFullname)
            .await()
        Log.d("FirestoreUtil", "Updated fullname for uid=$uid to $newFullname")
        true
    } catch (e: Exception) {
        Log.e("FirestoreUtil", "Error updating fullname: $e")
        false
    }
}
