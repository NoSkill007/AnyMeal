// --- PASO 1: DTOs de Red (Data Transfer Objects) ---
// Archivo: dto/UserDtos.kt
// Propósito: Contiene las clases para enviar los datos de perfil y contraseña al backend.
package com.noskill.anymeal.dto

import com.google.gson.annotations.SerializedName

// DTO para la petición de actualizar nombre y email.
data class UpdateProfileRequest(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String
)

// DTO para la petición de cambiar la contraseña.
data class ChangePasswordRequest(
    @SerializedName("oldPassword") val oldPassword: String,
    @SerializedName("newPassword") val newPassword: String,
    @SerializedName("confirmPassword") val confirmPassword: String
)

// DTO para recibir una respuesta genérica del servidor.
data class MessageResponse(
    @SerializedName("message") val message: String
)