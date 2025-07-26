// ========================================================================
// Archivo: data/model/User.kt
// ========================================================================
package com.noskill.anymeal.data.model

// Representa la respuesta del perfil de usuario desde el backend.
data class User(
    val id: Long,
    val username: String,
    val email: String
)