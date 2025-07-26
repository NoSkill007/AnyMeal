// ========================================================================
// Archivo: viewmodel/AuthViewModel.kt
// ========================================================================
package com.noskill.anymeal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.noskill.anymeal.data.local.SessionManager
import com.noskill.anymeal.data.repository.AuthRepository
import com.noskill.anymeal.di.NetworkModule
import com.noskill.anymeal.dto.AuthResponse
import com.noskill.anymeal.dto.LoginRequest
import com.noskill.anymeal.dto.RegisterRequest
import com.noskill.anymeal.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = NetworkModule.provideApiService(application)
    private val authRepository = AuthRepository(apiService)
    private val sessionManager = SessionManager(application)

    private val _authState = MutableStateFlow<Result<AuthResponse>>(Result.Success(AuthResponse("")))
    val authState: StateFlow<Result<AuthResponse>> = _authState

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _authState.value = Result.Loading
            try {
                val response = authRepository.login(request)
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token
                    sessionManager.saveAuthToken(token)
                    _authState.value = Result.Success(response.body()!!)
                } else {
                    _authState.value = Result.Error("Usuario o contraseña incorrectos.")
                }
            } catch (e: Exception) {
                _authState.value = Result.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _authState.value = Result.Loading
            try {
                val response = authRepository.register(request)
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token
                    sessionManager.saveAuthToken(token)
                    _authState.value = Result.Success(response.body()!!)
                } else {
                    _authState.value = Result.Error("No se pudo registrar. El usuario o email ya podría existir.")
                }
            } catch (e: Exception) {
                _authState.value = Result.Error("Error de conexión: ${e.message}")
            }
        }
    }
}