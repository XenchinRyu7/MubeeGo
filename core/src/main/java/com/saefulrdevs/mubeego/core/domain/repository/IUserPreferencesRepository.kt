package com.saefulrdevs.mubeego.core.domain.repository

import com.saefulrdevs.mubeego.core.domain.model.UserData

interface IUserPreferencesRepository {
    fun saveUser(uid: String, fullname: String, email: String)
    fun getUser(): UserData?
    fun clearUser()
    fun setThemeMode(mode: Int)
    fun getThemeMode(): Int
}