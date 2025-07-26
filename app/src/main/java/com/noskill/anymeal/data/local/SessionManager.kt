// ========================================================================
// Archivo: data/local/SessionManager.kt
// ========================================================================
package com.noskill.anymeal.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestiona el almacenamiento local del token de autenticación.
 * Utiliza SharedPreferences para persistir el token de forma sencilla.
 *
 * @param context El contexto de la aplicación, necesario para acceder a SharedPreferences.
 */
class SessionManager(context: Context) {

    private var prefs: SharedPreferences =
        context.getSharedPreferences("AnyMealAppPrefs", Context.MODE_PRIVATE)

    companion object {
        const val AUTH_TOKEN = "auth_token"
    }

    /**
     * Guarda el token de autenticación en SharedPreferences.
     * @param token El token JWT recibido del backend.
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(AUTH_TOKEN, token)
        editor.apply()
    }

    /**
     * Recupera el token de autenticación de SharedPreferences.
     * @return El token guardado, o null si no existe ninguno.
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(AUTH_TOKEN, null)
    }

    /**
     * Borra el token de autenticación.
     * Se debe llamar al cerrar sesión.
     */
    fun clearAuthToken() {
        val editor = prefs.edit()
        editor.remove(AUTH_TOKEN)
        editor.apply()
    }
}