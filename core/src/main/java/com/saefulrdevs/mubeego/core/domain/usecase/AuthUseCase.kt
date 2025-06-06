package com.saefulrdevs.mubeego.core.domain.usecase

import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.domain.model.UserData
import kotlinx.coroutines.flow.Flow

interface AuthUseCase {
    fun signUpWithEmail(fullname : String, email: String, password: String): Flow<Resource<Boolean>>
    fun signInWithGoogle(idToken: String): Flow<Resource<Boolean>>
    fun signInWithEmail(email: String, password: String): Flow<Resource<Boolean>>
    fun signOut(): Flow<Resource<Boolean>>
    fun getCurrentUser(): UserData?
    fun sendPasswordResetEmail(email: String): Flow<Resource<Boolean>>
}
