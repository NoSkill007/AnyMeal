// ========================================================================
// Archivo: UserRepository.kt
// Propósito: Gestiona las operaciones de red relacionadas con el usuario,
//            permitiendo obtener el perfil, actualizar datos y cambiar la contraseña.
// ========================================================================

// --- PASO 3: Repositorio de Usuario Actualizado ---
// Archivo: data/repository/UserRepository.kt
// Propósito: Añade los métodos para llamar a los nuevos endpoints de la API.
package com.noskill.anymeal.data.repository

import com.noskill.anymeal.data.network.ApiService
import com.noskill.anymeal.dto.ChangePasswordRequest
import com.noskill.anymeal.dto.UpdateProfileRequest

// UserRepository se encarga de interactuar con el ApiService para gestionar
// la información y acciones del usuario actual.
class UserRepository(private val apiService: ApiService) {
    // Obtiene el perfil del usuario actual.
    suspend fun getUserProfile() = apiService.getUserProfile()

    // Actualiza los datos del perfil del usuario.
    suspend fun updateProfile(request: UpdateProfileRequest) = apiService.updateProfile(request)

    // Cambia la contraseña del usuario actual.
    suspend fun changePassword(request: ChangePasswordRequest) = apiService.changePassword(request)
}