package com.saefulrdevs.mubeego.core.data

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.saefulrdevs.mubeego.core.domain.model.UserData
import com.saefulrdevs.mubeego.core.domain.repository.IUserPreferencesRepository
import androidx.core.content.edit

class UserPreferencesRepository(context: Context) : IUserPreferencesRepository {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    override fun saveUser(uid: String, fullname: String, email: String, isPremium: Boolean) {
        prefs.edit().apply {
            putString("UID", uid)
            putString("FULLNAME", fullname)
            putString("EMAIL", email)
            putBoolean("IS_PREMIUM", isPremium)
            apply()
        }
    }

    override fun getUser(): UserData? {
        val uid = prefs.getString("UID", null)
        val fullname = prefs.getString("FULLNAME", null)
        val email = prefs.getString("EMAIL", null)
        val isPremium = prefs.getBoolean("IS_PREMIUM", false)
        return if (uid != null && fullname != null && email != null) {
            UserData(uid, fullname, email, isPremium)
        } else null
    }

    override fun clearUser() {
        Firebase.auth.signOut()
        prefs.edit().clear().apply()
    }

    override fun setThemeMode(mode: Int) {
        prefs.edit().putInt("THEME_MODE", mode).apply()
    }

    override fun getThemeMode(): Int {
        return prefs.getInt("THEME_MODE", 0)
    }

    override fun setNotificationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("NOTIFICATION_ENABLED", enabled).apply()
    }

    override fun isNotificationEnabled(): Boolean {
        return prefs.getBoolean("NOTIFICATION_ENABLED", true)
    }

    override fun setOnboardingShown(shown: Boolean) {
        prefs.edit { putBoolean("ONBOARDING_SHOWN", shown) }
    }

    override fun isOnboardingShown(): Boolean {
        return prefs.getBoolean("ONBOARDING_SHOWN", false)
    }
}