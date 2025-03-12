package com.saefulrdevs.mubeego.core.domain.usecase

import com.saefulrdevs.mubeego.core.domain.model.UserData
import com.saefulrdevs.mubeego.core.domain.repository.IUserPreferencesRepository

class UserPreferencesInteractor(private val userPreferencesRepository: IUserPreferencesRepository) : UserPreferencesUseCase {
    override fun saveUser(uid: String, fullname: String, email: String) {
        userPreferencesRepository.saveUser(uid, fullname, email)
    }

    override fun getUser(): UserData? {
        return userPreferencesRepository.getUser()
    }

    override fun clearUser() {
        userPreferencesRepository.clearUser()
    }
}