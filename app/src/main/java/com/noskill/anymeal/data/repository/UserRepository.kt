// --- PASO 3: Repositorio de Usuario Actualizado ---
// Archivo: data/repository/UserRepository.kt
// Propósito: Añade los métodos para llamar a los nuevos endpoints de la API.
package com.noskill.anymeal.data.repository

import com.noskill.anymeal.data.network.ApiService
import com.noskill.anymeal.dto.ChangePasswordRequest
import com.noskill.anymeal.dto.UpdateProfileRequest

class UserRepository(private val apiService: ApiService) {
    suspend fun getUserProfile() = apiService.getUserProfile()
    suspend fun updateProfile(request: UpdateProfileRequest) = apiService.updateProfile(request)
    suspend fun changePassword(request: ChangePasswordRequest) = apiService.changePassword(request)
}