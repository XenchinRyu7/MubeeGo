package com.saefulrdevs.mubeego.core.data

import com.google.firebase.auth.FirebaseAuth
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
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        result.user?.let { user ->
            val profileUpdates = userProfileChangeRequest {
                displayName = fullname
            }
            user.updateProfile(profileUpdates).await()
            emit(Resource.Success(true))
        } ?: emit(Resource.Error("User creation failed"))
    }.catch { e ->
        emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
    }

    override fun signInWithGoogle(idToken: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        emit(Resource.Success(result.user != null))
    }.catch { e ->
        emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
    }

    override fun signInWithEmail(email: String, password: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        val result = auth.signInWithEmailAndPassword(email, password).await()
        emit(Resource.Success(result.user != null))
    }.catch { e ->
        emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
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