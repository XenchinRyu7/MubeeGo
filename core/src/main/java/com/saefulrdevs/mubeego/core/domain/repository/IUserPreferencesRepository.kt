package com.saefulrdevs.mubeego.core.domain.repository

import com.saefulrdevs.mubeego.core.domain.model.UserData

interface IUserPreferencesRepository {
    fun saveUser(uid: String, fullname: String, email: String, isPremium: Boolean)
    fun getUser(): UserData?
    fun clearUser()
    fun setThemeMode(mode: Int)
    fun getThemeMode(): Int
    fun setNotificationEnabled(enabled: Boolean)
    fun isNotificationEnabled(): Boolean
    fun setOnboardingShown(shown: Boolean)
    fun isOnboardingShown(): Boolean
}