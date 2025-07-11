package com.saefulrdevs.mubeego.core.domain.usecase

import com.saefulrdevs.mubeego.core.domain.model.UserData
import com.saefulrdevs.mubeego.core.domain.repository.IUserPreferencesRepository

class UserPreferencesInteractor(private val userPreferencesRepository: IUserPreferencesRepository) : UserPreferencesUseCase {
    override fun saveUser(uid: String, fullname: String, email: String, isPremium: Boolean) {
        userPreferencesRepository.saveUser(uid, fullname, email, isPremium)
    }

    override fun getUser(): UserData? {
        return userPreferencesRepository.getUser()
    }

    override fun clearUser() {
        userPreferencesRepository.clearUser()
    }

    override fun setThemeMode(mode: Int) {
        userPreferencesRepository.setThemeMode(mode)
    }

    override fun getThemeMode(): Int {
        return userPreferencesRepository.getThemeMode()
    }

    override fun setOnboardingShown(shown: Boolean) {
        userPreferencesRepository.setOnboardingShown(shown)
    }

    override fun isOnboardingShown(): Boolean {
        return userPreferencesRepository.isOnboardingShown()
    }
}