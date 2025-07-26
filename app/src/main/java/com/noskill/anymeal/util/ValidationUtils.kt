// ========================================================================
// Archivo: util/ValidationUtils.kt
// ========================================================================
package com.noskill.anymeal.util

import android.util.Patterns

// Función de utilidad para validar un correo electrónico.
fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}