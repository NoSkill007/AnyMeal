/*
Archivo: SessionManager.kt
Propósito: Gestiona el almacenamiento local del token de autenticación de usuario usando SharedPreferences. Permite guardar, recuperar y eliminar el token JWT para la sesión.
*/
package com.noskill.anymeal.data.local

import android.content.Context // Importa la clase Context para acceder a recursos de la app.
import android.content.SharedPreferences // Importa SharedPreferences para almacenamiento clave-valor.

/**
 * Gestiona el almacenamiento local del token de autenticación.
 * Utiliza SharedPreferences para persistir el token de forma sencilla.
 *
 * @param context El contexto de la aplicación, necesario para acceder a SharedPreferences.
 */
class SessionManager(context: Context) { // Define la clase SessionManager que gestiona el token de sesión.

    private var prefs: SharedPreferences =
        context.getSharedPreferences("AnyMealAppPrefs", Context.MODE_PRIVATE) // Inicializa SharedPreferences con nombre específico y modo privado.

    companion object {
        const val AUTH_TOKEN = "auth_token" // Clave constante para guardar el token de autenticación.
    }

    /**
     * Guarda el token de autenticación en SharedPreferences.
     * @param token El token JWT recibido del backend.
     */
    fun saveAuthToken(token: String) { // Guarda el token JWT en SharedPreferences.
        val editor = prefs.edit() // Obtiene el editor para modificar SharedPreferences.
        editor.putString(AUTH_TOKEN, token) // Asigna el token a la clave AUTH_TOKEN.
        editor.apply() // Aplica los cambios de forma asíncrona.
    }

    /**
     * Recupera el token de autenticación de SharedPreferences.
     * @return El token guardado, o null si no existe ninguno.
     */
    fun fetchAuthToken(): String? { // Recupera el token guardado, retorna null si no existe.
        return prefs.getString(AUTH_TOKEN, null) // Obtiene el valor asociado a AUTH_TOKEN.
    }

    /**
     * Borra el token de autenticación.
     * Se debe llamar al cerrar sesión.
     */
    fun clearAuthToken() { // Elimina el token de autenticación, útil para cerrar sesión.
        val editor = prefs.edit() // Obtiene el editor para modificar SharedPreferences.
        editor.remove(AUTH_TOKEN) // Elimina el valor asociado a AUTH_TOKEN.
        editor.apply() // Aplica los cambios de forma asíncrona.
    }
}