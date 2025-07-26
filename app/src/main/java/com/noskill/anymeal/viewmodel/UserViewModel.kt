// ========================================================================
// Archivo: viewmodel/UserViewModel.kt
// (Sin cambios, pero se incluye para consistencia)
// ========================================================================
package com.noskill.anymeal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.noskill.anymeal.data.model.User
import com.noskill.anymeal.data.repository.UserRepository
import com.noskill.anymeal.di.NetworkModule
import com.noskill.anymeal.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(NetworkModule.provideApiService(application))

    private val _userState = MutableStateFlow<Result<User?>>(Result.Loading)
    val userState: StateFlow<Result<User?>> = _userState

    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            _userState.value = Result.Loading
            try {
                val response = userRepository.getUserProfile()
                if (response.isSuccessful && response.body() != null) {
                    _userState.value = Result.Success(response.body())
                } else {
                    _userState.value = Result.Error("No se pudo obtener el perfil del usuario.")
                }
            } catch (e: Exception) {
                _userState.value = Result.Error("Error de conexión: ${e.message}")
            }
        }
    }
}