package com.noskill.anymeal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.noskill.anymeal.data.local.SessionManager // Importar SessionManager
import com.noskill.anymeal.data.model.User
import com.noskill.anymeal.data.repository.UserRepository
import com.noskill.anymeal.di.NetworkModule
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

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = NetworkModule.provideApiService(application)
    private val userRepository = UserRepository(apiService)
    private val sessionManager = SessionManager(application) // <-- NUEVO: Instancia de SessionManager

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = userRepository.getUserProfile()
                if (response.isSuccessful && response.body() != null) {
                    val userResponse = response.body()!!
                    val user = User(userResponse.id, userResponse.username, userResponse.email)
                    _uiState.update { it.copy(isLoading = false, user = user) }
                } else {
                    // Si la carga del perfil falla (ej. token inválido), podría requerir reautenticación
                    if (response.code() == 401 || response.code() == 403) { // Códigos comunes para no autorizado/prohibido
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Sesión expirada. Por favor, inicia sesión de nuevo.", requiresReauthentication = true) }
                        sessionManager.clearAuthToken() // Limpiar token inválido
                    } else {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Error al cargar el perfil: ${response.code()}") }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error de conexión: ${e.message}") }
            }
        }
    }

    fun updateProfileData(newName: String, newEmail: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(profileSaveState = SaveState.LOADING, errorMessage = null) }
            try {
                val request = UpdateProfileRequest(newName, newEmail)
                val response = userRepository.updateProfile(request)
                if (response.isSuccessful) {
                    _uiState.update { it.copy(profileSaveState = SaveState.SUCCESS) }
                    // Si el email cambia y el backend invalida la sesión, forzar reautenticación
                    if (_uiState.value.user?.email != newEmail) { // Si el email realmente cambió
                        _uiState.update { it.copy(requiresReauthentication = true, errorMessage = "Email actualizado. Por favor, inicia sesión de nuevo.") }
                        sessionManager.clearAuthToken() // Limpiar el token antiguo
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    // Si el error es por token inválido, forzar reautenticación
                    if (response.code() == 401 || response.code() == 403) {
                        _uiState.update { it.copy(profileSaveState = SaveState.ERROR, errorMessage = "Sesión expirada. Por favor, inicia sesión de nuevo.", requiresReauthentication = true) }
                        sessionManager.clearAuthToken()
                    } else {
                        _uiState.update { it.copy(profileSaveState = SaveState.ERROR, errorMessage = "Error al actualizar perfil: $errorBody") }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(profileSaveState = SaveState.ERROR, errorMessage = "Error de conexión: ${e.message}") }
            }
        }
    }

    fun changePassword(oldPass: String, newPass: String, confirmPass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(passwordSaveState = SaveState.LOADING, errorMessage = null) }
            if (newPass != confirmPass) {
                _uiState.update { it.copy(passwordSaveState = SaveState.ERROR, errorMessage = "Las nuevas contraseñas no coinciden.") }
                return@launch
            }
            try {
                val request = ChangePasswordRequest(oldPass, newPass, confirmPass)
                val response = userRepository.changePassword(request)
                if (response.isSuccessful) {
                    _uiState.update { it.copy(passwordSaveState = SaveState.SUCCESS) }
                    // Después de cambiar la contraseña, SIEMPRE forzar reautenticación por seguridad
                    _uiState.update { it.copy(requiresReauthentication = true, errorMessage = "Contraseña cambiada. Por favor, inicia sesión de nuevo.") }
                    sessionManager.clearAuthToken() // Limpiar el token antiguo
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    // Si el error es por token inválido o credenciales incorrectas, forzar reautenticación
                    if (response.code() == 401 || response.code() == 403) {
                        _uiState.update { it.copy(passwordSaveState = SaveState.ERROR, errorMessage = "Sesión expirada. Por favor, inicia sesión de nuevo.", requiresReauthentication = true) }
                        sessionManager.clearAuthToken()
                    } else {
                        _uiState.update { it.copy(passwordSaveState = SaveState.ERROR, errorMessage = "Error al cambiar contraseña: $errorBody") }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(passwordSaveState = SaveState.ERROR, errorMessage = "Error de conexión: ${e.message}") }
            }
        }
    }

    fun resetSaveStates() {
        _uiState.update { it.copy(profileSaveState = SaveState.IDLE, passwordSaveState = SaveState.IDLE, errorMessage = null) }
    }

    // NUEVO: Función para resetear la bandera de reautenticación
    fun resetReauthenticationFlag() {
        _uiState.update { it.copy(requiresReauthentication = false) }
    }
}