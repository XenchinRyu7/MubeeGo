package com.saefulrdevs.mubeego.core.domain.usecase

import com.saefulrdevs.mubeego.core.domain.model.UserData

interface UserPreferencesUseCase {
    fun saveUser(uid: String, fullname: String, email: String)
    fun getUser(): UserData?
    fun clearUser()
    fun setThemeMode(mode: Int)
    fun getThemeMode(): Int
    fun setNotificationEnabled(enabled: Boolean)
    fun isNotificationEnabled(): Boolean
}