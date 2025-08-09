/* --------------------------------------------------------------------
 * Archivo: AuthResponse.kt
 * Propósito: Define el modelo de datos para la respuesta de autenticación
 *            recibida desde el backend, incluyendo el token JWT del usuario.
 * --------------------------------------------------------------------*/

package com.noskill.anymeal.dto

// Modelo de datos para la respuesta de autenticación.
// Contiene el token JWT que se utiliza para autenticar las peticiones del usuario.
data class AuthResponse(
    val token: String // Token JWT recibido tras autenticación exitosa
)
