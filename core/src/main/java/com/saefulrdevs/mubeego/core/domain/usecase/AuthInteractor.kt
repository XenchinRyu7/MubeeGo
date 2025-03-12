package com.saefulrdevs.mubeego.core.domain.usecase

import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.domain.model.UserData
import com.saefulrdevs.mubeego.core.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow

class AuthInteractor(private val authRepository: IAuthRepository) : AuthUseCase {
    override fun signUpWithEmail(fullname: String, email: String, password: String): Flow<Resource<Boolean>> {
        return authRepository.signUpWithEmail(fullname, email, password)
    }

    override fun signInWithGoogle(idToken: String): Flow<Resource<Boolean>> {
        return authRepository.signInWithGoogle(idToken)
    }

    override fun signInWithEmail(email: String, password: String): Flow<Resource<Boolean>> {
        return authRepository.signInWithEmail(email, password)
    }

    override fun signOut(): Flow<Resource<Boolean>> {
        return authRepository.signOut()
    }

    override fun getCurrentUser(): UserData? {
        return authRepository.getCurrentUser()
    }
}