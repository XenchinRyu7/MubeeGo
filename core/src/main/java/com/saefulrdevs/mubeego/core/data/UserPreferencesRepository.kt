package com.saefulrdevs.mubeego.core.data

import android.content.Context
import android.content.SharedPreferences
import com.saefulrdevs.mubeego.core.domain.model.UserData
import com.saefulrdevs.mubeego.core.domain.repository.IUserPreferencesRepository

class UserPreferencesRepository(context: Context) : IUserPreferencesRepository {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    override fun saveUser(uid: String, fullname: String, email: String) {
        prefs.edit().apply {
            putString("UID", uid)
            putString("FULLNAME", fullname)
            putString("EMAIL", email)
            apply()
        }
    }

    override fun getUser(): UserData? {
        val uid = prefs.getString("UID", null)
        val fullname = prefs.getString("FULLNAME", null)
        val email = prefs.getString("EMAIL", null)
        return if (uid != null && fullname != null && email != null) {
            UserData(uid, fullname, email)
        } else null
    }

    override fun clearUser() {
        prefs.edit().clear().apply()
    }
}