package com.saefulrdevs.mubeego.ui.authentication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.domain.usecase.AuthUseCase
import com.saefulrdevs.mubeego.core.domain.usecase.UserPreferencesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AuthViewModel(
    private val authUseCase: AuthUseCase,
    private val userPreferencesUseCase: UserPreferencesUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<Resource<Boolean>>(Resource.Loading())
    val authState: StateFlow<Resource<Boolean>> = _authState

    fun signUpWithEmail(fullname: String, email: String, password: String) {
        authUseCase.signUpWithEmail(
            fullname, email, password
        ).onEach { resource ->
            _authState.value = resource
        }.launchIn(viewModelScope)
    }

    fun signInWithGoogle(idToken: String) {
        Log.d("AuthViewModel", "Memulai sign-in dengan Google, ID Token: $idToken")

        authUseCase.signInWithGoogle(idToken).onEach { resource ->
            Log.d("AuthViewModel", "Hasil Sign-In: $resource")
            _authState.value = resource

            if (resource is Resource.Success) {
                val user = authUseCase.getCurrentUser()
                Log.d("AuthViewModel", "User setelah login: $user")

                user?.let {
                    Log.d("AuthViewModel", "Menyimpan user ke preferences: ${it.uid}, ${it.fullname}, ${it.email}")
                    userPreferencesUseCase.saveUser(it.uid, it.fullname ?: "", it.email)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun signInWithEmail(email: String, password: String) {
        authUseCase.signInWithEmail(email, password).onEach { resource ->
            _authState.value = resource
        }.launchIn(viewModelScope)
    }

    fun getUser() = userPreferencesUseCase.getUser()

    fun signOut() {
        authUseCase.signOut().onEach { resource ->
            _authState.value = resource
            if (resource is Resource.Success) {
                userPreferencesUseCase.clearUser()
            }
        }.launchIn(viewModelScope)
    }
}