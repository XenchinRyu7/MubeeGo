package com.saefulrdevs.mubeego.core.domain.repository

import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.domain.model.UserData
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    fun signUpWithEmail(fullname: String, email: String, password: String): Flow<Resource<Boolean>>
    fun signInWithGoogle(idToken: String): Flow<Resource<Boolean>>
    fun signInWithEmail(email: String, password: String): Flow<Resource<Boolean>>
    fun signOut(): Flow<Resource<Boolean>>
    fun getCurrentUser(): UserData?
}