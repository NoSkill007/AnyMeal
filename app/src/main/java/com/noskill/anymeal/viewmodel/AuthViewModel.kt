/**
 * AuthViewModel.kt
 *
 * Propósito: Gestiona la autenticación de usuarios en la aplicación, incluyendo procesos de
 * inicio de sesión, registro y mantenimiento del estado de autenticación. Actúa como intermediario
 * entre la interfaz de usuario y el repositorio de autenticación.
 */
package com.noskill.anymeal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.noskill.anymeal.data.local.SessionManager
import com.noskill.anymeal.data.repository.AuthRepository
import com.noskill.anymeal.data.di.NetworkModule
import com.noskill.anymeal.dto.AuthResponse
import com.noskill.anymeal.dto.LoginRequest
import com.noskill.anymeal.dto.RegisterRequest
import com.noskill.anymeal.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que maneja toda la lógica relacionada con la autenticación de usuarios.
 * Extiende AndroidViewModel para tener acceso al contexto de la aplicación.
 *
 * @param application Instancia de la aplicación para acceder al contexto
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Servicio de API para comunicación con el backend de autenticación
     */
    private val apiService = NetworkModule.provideApiService(application)

    /**
     * Repositorio que encapsula la lógica de operaciones de autenticación
     */
    private val authRepository = AuthRepository(apiService)

    /**
     * Gestor de sesión para almacenar tokens de autenticación localmente
     */
    private val sessionManager = SessionManager(application)

    /**
     * Estado interno mutable que representa el resultado de las operaciones de autenticación
     */
    private val _authState = MutableStateFlow<Result<AuthResponse>>(Result.Success(AuthResponse("")))

    /**
     * Estado público de autenticación observable desde la UI.
     * Contiene el resultado de las operaciones de autenticación (éxito, error o carga).
     */
    val authState: StateFlow<Result<AuthResponse>> = _authState

    /**
     * Realiza el proceso de inicio de sesión con las credenciales proporcionadas.
     * Actualiza el estado de autenticación y guarda el token en sesión si es exitoso.
     *
     * @param request Objeto con las credenciales de inicio de sesión (usuario/email y contraseña)
     */
    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _authState.value = Result.Loading // Indica que el proceso de login está en curso
            try {
                val response = authRepository.login(request)
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token // Obtiene el token de autenticación
                    sessionManager.saveAuthToken(token) // Almacena el token para futuras solicitudes
                    _authState.value = Result.Success(response.body()!!) // Notifica éxito a la UI
                } else {
                    _authState.value = Result.Error("Usuario o contraseña incorrectos.") // Notifica error de credenciales
                }
            } catch (e: Exception) {
                _authState.value = Result.Error("Error de conexión: ${e.message}") // Notifica error de red
            }
        }
    }

    /**
     * Realiza el proceso de registro de un nuevo usuario con la información proporcionada.
     * Actualiza el estado de autenticación y guarda el token en sesión si es exitoso.
     *
     * @param request Objeto con los datos de registro (nombre, email, contraseña, etc.)
     */
    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _authState.value = Result.Loading // Indica que el proceso de registro está en curso
            try {
                val response = authRepository.register(request)
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token // Obtiene el token de autenticación
                    sessionManager.saveAuthToken(token) // Almacena el token para futuras solicitudes
                    _authState.value = Result.Success(response.body()!!) // Notifica éxito a la UI
                } else {
                    _authState.value = Result.Error("No se pudo registrar. El usuario o email ya podría existir.") // Notifica error de registro
                }
            } catch (e: Exception) {
                _authState.value = Result.Error("Error de conexión: ${e.message}") // Notifica error de red
            }
        }
    }
}
