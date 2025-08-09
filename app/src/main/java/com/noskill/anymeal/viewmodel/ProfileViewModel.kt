// ========================================================================
// Archivo: ProfileViewModel.kt
// Propósito: Gestiona el estado y la lógica relacionada con el perfil de usuario.
//            Permite obtener, actualizar el perfil y cambiar la contraseña,
//            manejando los estados de carga, éxito, error y reautenticación.
// ========================================================================

package com.noskill.anymeal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.noskill.anymeal.data.local.SessionManager // Importar SessionManager
import com.noskill.anymeal.data.model.User
import com.noskill.anymeal.data.repository.UserRepository
import com.noskill.anymeal.data.di.NetworkModule
import com.noskill.anymeal.dto.ChangePasswordRequest
import com.noskill.anymeal.dto.UpdateProfileRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Define los posibles estados de una operación de guardado (Inactivo, Cargando, Éxito, Error)
enum class SaveState { IDLE, LOADING, SUCCESS, ERROR }

// El estado de la UI ahora incluye los datos del usuario y los estados de guardado.
data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val profileSaveState: SaveState = SaveState.IDLE,
    val passwordSaveState: SaveState = SaveState.IDLE,
    val errorMessage: String? = null,
    val requiresReauthentication: Boolean = false // <-- NUEVO: Bandera para reautenticación
)

// ProfileViewModel extiende AndroidViewModel y gestiona el estado del perfil de usuario.
class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    // Instancia del servicio de red y repositorio de usuario.
    private val apiService = NetworkModule.provideApiService(application)
    private val userRepository = UserRepository(apiService)
    private val sessionManager = SessionManager(application) // Instancia de SessionManager para manejar el token

    // StateFlow privado para almacenar el estado de la UI del perfil.
    private val _uiState = MutableStateFlow(ProfileUiState())
    // StateFlow público para observar el estado de la UI desde la interfaz.
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // Al inicializar el ViewModel, se obtiene el perfil del usuario automáticamente.
    init {
        fetchUserProfile()
    }

    // Método para obtener el perfil del usuario desde el repositorio.
    // Actualiza el estado según el resultado de la llamada a la API.
    fun fetchUserProfile() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = userRepository.getUserProfile()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { it.copy(isLoading = false, user = response.body()) }
                } else if (response.code() == 401) {
                    // Si el token expiró, se requiere reautenticación
                    _uiState.update { it.copy(isLoading = false, requiresReauthentication = true, errorMessage = "Sesión expirada. Inicia sesión nuevamente.") }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "No se pudo obtener el perfil del usuario.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error de conexión: ${e.message}") }
            }
        }
    }

    // Método para actualizar el perfil del usuario.
    // Actualiza el estado de guardado y maneja errores y éxito.
    fun updateProfile(username: String, email: String) {
        _uiState.update { it.copy(profileSaveState = SaveState.LOADING, errorMessage = null) }
        viewModelScope.launch {
            try {
                val request = UpdateProfileRequest(username, email)
                val response = userRepository.updateProfile(request)
                if (response.isSuccessful) {
                    _uiState.update { it.copy(profileSaveState = SaveState.SUCCESS) }
                    fetchUserProfile() // Actualiza el perfil en la UI
                } else if (response.code() == 401) {
                    _uiState.update { it.copy(profileSaveState = SaveState.ERROR, requiresReauthentication = true, errorMessage = "Sesión expirada. Inicia sesión nuevamente.") }
                } else {
                    _uiState.update { it.copy(profileSaveState = SaveState.ERROR, errorMessage = "No se pudo actualizar el perfil.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(profileSaveState = SaveState.ERROR, errorMessage = "Error de conexión: ${e.message}") }
            }
        }
    }

    // Método para cambiar la contraseña del usuario.
    // Actualiza el estado de guardado y maneja errores y éxito.
    fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String) {
        _uiState.update { it.copy(passwordSaveState = SaveState.LOADING, errorMessage = null) }
        viewModelScope.launch {
            try {
                val request = ChangePasswordRequest(oldPassword, newPassword, confirmPassword)
                val response = userRepository.changePassword(request)
                if (response.isSuccessful) {
                    _uiState.update { it.copy(passwordSaveState = SaveState.SUCCESS) }
                } else if (response.code() == 401) {
                    _uiState.update { it.copy(passwordSaveState = SaveState.ERROR, requiresReauthentication = true, errorMessage = "Sesión expirada. Inicia sesión nuevamente.") }
                } else {
                    _uiState.update { it.copy(passwordSaveState = SaveState.ERROR, errorMessage = "No se pudo cambiar la contraseña.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(passwordSaveState = SaveState.ERROR, errorMessage = "Error de conexión: ${e.message}") }
            }
        }
    }

    // Método para limpiar los estados de guardado y mensajes de error.
    fun clearSaveStates() {
        _uiState.update { it.copy(profileSaveState = SaveState.IDLE, passwordSaveState = SaveState.IDLE, errorMessage = null, requiresReauthentication = false) }
    }

    // FUNCIONES FALTANTES - Agregadas para solucionar errores en EditProfileScreen

    // Función para actualizar datos del perfil (alias de updateProfile para compatibilidad)
    fun updateProfileData(username: String, email: String) {
        updateProfile(username, email)
    }

    // Función para resetear la bandera de reautenticación
    fun resetReauthenticationFlag() {
        _uiState.update { it.copy(requiresReauthentication = false) }
    }

    // Función para resetear solo los estados de guardado (alias de clearSaveStates)
    fun resetSaveStates() {
        _uiState.update { it.copy(profileSaveState = SaveState.IDLE, passwordSaveState = SaveState.IDLE, errorMessage = null) }
    }

    // Función para cargar el perfil de usuario (alias de fetchUserProfile)
    fun loadUserProfile() {
        fetchUserProfile()
    }
}
