package com.saefulrdevs.mubeego.core.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.saefulrdevs.mubeego.core.domain.model.UserData
import com.saefulrdevs.mubeego.core.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth) : IAuthRepository {

    override fun signUpWithEmail(fullname: String, email: String, password: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                val profileUpdates = userProfileChangeRequest {
                    displayName = fullname
                }
                user.updateProfile(profileUpdates).await()
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
            result.user?.let {
                emit(Resource.Success(true))
            } ?: emit(Resource.Error("Sign-in dengan Google gagal"))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e("FirebaseAuth", "Invalid Google credential: ${e.message}")
            emit(Resource.Error("Token Google tidak valid!"))
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.e("FirebaseAuth", "Google account already linked: ${e.message}")
            emit(Resource.Error("Akun Google sudah terhubung dengan akun lain!"))
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
            result.user?.let {
                emit(Resource.Success(true))
            } ?: emit(Resource.Error("Sign-in gagal, user tidak ditemukan"))
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

    override fun getCurrentUser(): UserData? {
        return auth.currentUser?.let { user ->
            UserData(user.uid, user.displayName ?: "Unknown", user.email ?: "")
        }
    }
}