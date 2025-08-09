/* --------------------------------------------------------------------
 * Archivo: RegisterRequest.kt
 * Propósito: Define el modelo de datos para la petición de registro de usuario,
 *            utilizado para enviar los datos necesarios al backend y crear una cuenta nueva.
 * --------------------------------------------------------------------*/

package com.noskill.anymeal.dto

// Modelo de datos para la petición de registro de usuario.
// Contiene el nombre de usuario, correo electrónico y contraseña que serán enviados al servidor.
data class RegisterRequest(
    val username: String, // Nombre de usuario del nuevo usuario
    val email: String,    // Correo electrónico del nuevo usuario
    val password: String  // Contraseña del nuevo usuario
)