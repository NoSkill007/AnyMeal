// ========================================================================
// Archivo: AuthRepository.kt
// Propósito: Proporciona la lógica de autenticación interactuando con el ApiService.
// ========================================================================
package com.noskill.anymeal.data.repository

import com.noskill.anymeal.data.network.ApiService
import com.noskill.anymeal.dto.LoginRequest
import com.noskill.anymeal.dto.RegisterRequest

// AuthRepository es responsable de manejar las operaciones de autenticación.
// Se comunica con el ApiService remoto para realizar el inicio de sesión y el registro.
class AuthRepository(private val apiService: ApiService) {
    // Realiza el inicio de sesión del usuario enviando un LoginRequest al ApiService.
    // Devuelve el resultado de la llamada a la API (usualmente un objeto de respuesta).
    suspend fun login(request: LoginRequest) = apiService.login(request)

    // Registra un nuevo usuario enviando un RegisterRequest al ApiService.
    // Devuelve el resultado de la llamada a la API (usualmente un objeto de respuesta).
    suspend fun register(request: RegisterRequest) = apiService.register(request)
}