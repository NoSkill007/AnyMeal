// ========================================================================
// Archivo: data/repository/AuthRepository.kt
// ========================================================================
package com.noskill.anymeal.data.repository

import com.noskill.anymeal.data.network.ApiService
import com.noskill.anymeal.dto.LoginRequest
import com.noskill.anymeal.dto.RegisterRequest

// Repositorio para manejar la lógica de autenticación.
class AuthRepository(private val apiService: ApiService) {
    suspend fun login(request: LoginRequest) = apiService.login(request)
    suspend fun register(request: RegisterRequest) = apiService.register(request)
}