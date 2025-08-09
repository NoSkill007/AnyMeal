/* --------------------------------------------------------------------
 * Archivo: LoginRequest.kt
 * Propósito: Define el modelo de datos para la petición de inicio de sesión,
 *            utilizado para enviar las credenciales del usuario al backend.
 * --------------------------------------------------------------------*/

package com.noskill.anymeal.dto

// Modelo de datos para la petición de inicio de sesión.
// Contiene el nombre de usuario y la contraseña que serán enviados al servidor.
data class LoginRequest(
    val username: String, // Nombre de usuario del usuario
    val password: String  // Contraseña del usuario
)
