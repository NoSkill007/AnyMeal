/**
 * ValidationUtils.kt
 * 
 * Propósito: Proporciona funciones de utilidad para validar diferentes tipos de datos
 * ingresados por el usuario, como direcciones de correo electrónico, contraseñas, etc.
 * Estas funciones son utilizadas en toda la aplicación para garantizar la integridad
 * de los datos antes de procesarlos.
 */
package com.noskill.anymeal.util

import android.util.Patterns

/**
 * Valida si una cadena de texto corresponde a un formato válido de dirección de correo electrónico.
 * Utiliza el patrón predefinido de Android para validación de emails.
 *
 * @param email La cadena de texto a validar como dirección de correo electrónico
 * @return true si el formato del email es válido, false en caso contrario
 */
fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
