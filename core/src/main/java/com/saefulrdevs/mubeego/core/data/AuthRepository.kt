package com.saefulrdevs.mubeego.core.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.saefulrdevs.mubeego.core.domain.model.UserData
import com.saefulrdevs.mubeego.core.domain.model.toMap
import com.saefulrdevs.mubeego.core.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth, private val firestore: FirebaseFirestore) :
    IAuthRepository {

    override fun signUpWithEmail(
        fullname: String,
        email: String,
        password: String
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                val profileUpdates = userProfileChangeRequest {
                    displayName = fullname
                }
                user.updateProfile(profileUpdates).await()
                user.sendEmailVerification().await()
                emit(Resource.Success(true))
            } ?: emit(Resource.Error("User creation failed"))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e("FirebaseAuth", "Invalid email format: ${e.message}")
            emit(Resource.Error("Format email tidak valid!"))
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.e("FirebaseAuth", "Email already in use: ${e.message}")
            emit(Resource.Error("Email sudah digunakan!"))
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Error: ${e.message}")
            emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    override fun signInWithGoogle(idToken: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user

            if (user != null) {
                // Cek apakah data user sudah ada di Firestore
                val userDoc = firestore.collection("users").document(user.uid).get().await()
                if (!userDoc.exists()) {
                    val userMap = mapOf(
                        "uid" to user.uid,
                        "fullname" to (user.displayName ?: "No Name"),
                        "email" to (user.email ?: ""),
                        "isPremium" to false,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                    firestore.collection("users").document(user.uid).set(userMap).await()
                }
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Sign-in dengan Google gagal"))
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e("FirebaseAuth", "Invalid Google credential: ${e.message}")
            emit(Resource.Error("Token Google tidak valid!"))
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.e("FirebaseAuth", "Google account already linked: ", e)
            emit(Resource.Error("collision:${e.email ?: ""}"))
        } catch (e: FirebaseAuthException) {
            Log.e("FirebaseAuth", "Firebase error: ${e.message}")
            emit(Resource.Error("Terjadi kesalahan autentikasi Firebase"))
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Error: ${e.message}")
            emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    override fun signInWithEmail(email: String, password: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                if (!user.isEmailVerified) {
                    emit(Resource.Error("Silakan verifikasi email Anda terlebih dahulu."))
                    return@flow
                }
                // Cek apakah user sudah punya dokumen di Firestore
                val userDoc = firestore.collection("users").document(user.uid).get().await()
                if (!userDoc.exists()) {
                    val userMap = mapOf(
                        "uid" to user.uid,
                        "fullname" to (user.displayName ?: "No Name"),
                        "email" to (user.email ?: email),
                        "isPremium" to false,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                    firestore.collection("users").document(user.uid).set(userMap).await()
                }
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Sign-in gagal, user tidak ditemukan"))
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e("FirebaseAuth", "Invalid credentials: ${e.message}")
            emit(Resource.Error("Email atau password salah!"))
        } catch (e: FirebaseAuthInvalidUserException) {
            Log.e("FirebaseAuth", "User not found: ${e.message}")
            emit(Resource.Error("Akun tidak ditemukan!"))
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Error: ${e.message}")
            emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
        }
    }


    override fun signOut(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        auth.signOut()
        emit(Resource.Success(true))
    }

    private suspend fun fetchIsPremium(uid: String): Boolean {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            doc.getBoolean("isPremium") == true
        } catch (e: Exception) {
            false
        }
    }

    override fun getCurrentUser(): UserData? {
        return auth.currentUser?.let { user ->
            var isPremium = false
            try {
                val doc = firestore.collection("users").document(user.uid).get().getResult()
                isPremium = doc?.getBoolean("isPremium") ?: false
            } catch (e: Exception) {
                isPremium = false
            }
            UserData(user.uid, user.displayName ?: "Unknown", user.email ?: "", isPremium)
        }
    }

    override fun sendPasswordResetEmail(email: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            auth.sendPasswordResetEmail(email).await()
            emit(Resource.Success(true))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e("FirebaseAuth", "Invalid email format: ${e.message}")
            emit(Resource.Error("Format email tidak valid!"))
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Error: ${e.message}")
            emit(Resource.Error("Terjadi kesalahan. Silakan coba lagi."))
        }
    }

    override suspend fun createUserFirestoreAfterVerified(fullname: String): Resource<Boolean> {
        val user = auth.currentUser
        return if (user != null && user.isEmailVerified) {
            val userMap = mapOf(
                "uid" to user.uid,
                "fullname" to fullname,
                "email" to (user.email ?: ""),
                "isPremium" to false,
                "createdAt" to FieldValue.serverTimestamp()
            )
            return try {
                firestore.collection("users").document(user.uid).set(userMap).await()
                Resource.Success(true)
            } catch (e: Exception) {
                Resource.Error(e.localizedMessage ?: "Gagal menyimpan user ke Firestore")
            }
        } else {
            Resource.Error("Email belum diverifikasi")
        }
    }
}