// ========================================================================
// Archivo: UserViewModel.kt
// Propósito: Gestiona el estado y la lógica relacionada con el perfil de usuario.
//            Permite obtener el perfil desde el repositorio y expone el resultado
//            mediante un StateFlow para su uso en la interfaz de usuario.
// ========================================================================

package com.noskill.anymeal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.noskill.anymeal.data.model.User
import com.noskill.anymeal.data.repository.UserRepository
import com.noskill.anymeal.data.di.NetworkModule
import com.noskill.anymeal.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// UserViewModel extiende AndroidViewModel y gestiona el estado del perfil de usuario.
class UserViewModel(application: Application) : AndroidViewModel(application) {

    // Instancia del repositorio de usuario, inicializada con el ApiService proporcionado por NetworkModule.
    private val userRepository = UserRepository(NetworkModule.provideApiService(application))

    // StateFlow privado para almacenar el estado del usuario (cargando, éxito, error).
    private val _userState = MutableStateFlow<Result<User?>>(Result.Loading)
    // StateFlow público para observar el estado del usuario desde la UI.
    val userState: StateFlow<Result<User?>> = _userState

    // Al inicializar el ViewModel, se obtiene el perfil del usuario automáticamente.
    init {
        fetchUserProfile()
    }

    // Método para obtener el perfil del usuario desde el repositorio.
    // Actualiza el estado según el resultado de la llamada a la API.
    fun fetchUserProfile() {
        viewModelScope.launch {
            _userState.value = Result.Loading // Indica que la petición está en curso.
            try {
                val response = userRepository.getUserProfile()
                if (response.isSuccessful && response.body() != null) {
                    _userState.value = Result.Success(response.body()) // Perfil obtenido correctamente.
                } else {
                    _userState.value = Result.Error("No se pudo obtener el perfil del usuario.") // Error en la respuesta.
                }
            } catch (e: Exception) {
                _userState.value = Result.Error("Error de conexión: ${e.message}") // Error de red o excepción.
            }
        }
    }
}