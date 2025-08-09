/* --------------------------------------------------------------------
 * Archivo: UserDtos.kt
 * Propósito: Define los modelos de datos (DTOs) para las operaciones relacionadas
 *            con el usuario, incluyendo la actualización de perfil, cambio de contraseña
 *            y la respuesta genérica del servidor.
 * --------------------------------------------------------------------*/

package com.noskill.anymeal.dto

import com.google.gson.annotations.SerializedName

// DTO para la petición de actualizar nombre y email del usuario.
data class UpdateProfileRequest(
    @SerializedName("username") val username: String, // Nuevo nombre de usuario
    @SerializedName("email") val email: String        // Nuevo correo electrónico
)

// DTO para la petición de cambiar la contraseña del usuario.
data class ChangePasswordRequest(
    @SerializedName("oldPassword") val oldPassword: String,     // Contraseña actual
    @SerializedName("newPassword") val newPassword: String,     // Nueva contraseña
    @SerializedName("confirmPassword") val confirmPassword: String // Confirmación de la nueva contraseña
)

// DTO para recibir una respuesta genérica del servidor (por ejemplo, mensajes de éxito o error).
data class MessageResponse(
    @SerializedName("message") val message: String // Mensaje recibido del servidor
)