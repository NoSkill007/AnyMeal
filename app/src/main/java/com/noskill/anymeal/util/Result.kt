// ========================================================================
// Archivo: util/Result.kt
// ========================================================================
package com.noskill.anymeal.util

// Clase genérica para manejar los estados de una petición de red.
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}