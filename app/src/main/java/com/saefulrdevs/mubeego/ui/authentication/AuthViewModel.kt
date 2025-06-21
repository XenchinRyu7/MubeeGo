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
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authUseCase: AuthUseCase,
    private val userPreferencesUseCase: UserPreferencesUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<Resource<Boolean>>(Resource.Loading())
    val authState: StateFlow<Resource<Boolean>> = _authState

    private val _resetPasswordState = MutableStateFlow<Resource<Boolean>?>(null)
    val resetPasswordState: StateFlow<Resource<Boolean>?> = _resetPasswordState

    fun signUpWithEmail(fullname: String, email: String, password: String) {
        authUseCase.signUpWithEmail(
            fullname = fullname, email = email, password = password
        ).onEach { resource ->
            _authState.value = resource
        }.launchIn(viewModelScope)
    }

    fun signInWithGoogle(idToken: String) {
        Log.d("AuthViewModel", "Memulai sign-in dengan Google, ID Token: $idToken")

        authUseCase.signInWithGoogle(idToken).onEach { resource ->
            _authState.value = resource

            if (resource is Resource.Success) {
                val user = authUseCase.getCurrentUser()

                user?.let {
                    userPreferencesUseCase.saveUser(it.uid, it.fullname, it.email, it.isPremium)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun signInWithEmail(email: String, password: String) {
        authUseCase.signInWithEmail(email, password).onEach { resource ->
            _authState.value = resource

            if (resource is Resource.Success) {
                val user = authUseCase.getCurrentUser()

                user?.let {
                    userPreferencesUseCase.saveUser(it.uid, it.fullname, it.email, it.isPremium)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun sendPasswordResetEmail(email: String) {
        authUseCase.sendPasswordResetEmail(email).onEach { resource ->
            _resetPasswordState.value = resource
        }.launchIn(viewModelScope)
    }

    fun signOut() {
        authUseCase.signOut().onEach { resource ->
            _authState.value = resource
            if (resource is Resource.Success) {
                userPreferencesUseCase.clearUser()
            }
        }.launchIn(viewModelScope)
    }

    fun createUserFirestoreAfterVerified(fullname: String) = viewModelScope.launch {
        _authState.value = Resource.Loading()
        val result = authUseCase.createUserFirestoreAfterVerified(fullname)
        _authState.value = result
    }
}