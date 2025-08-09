// ========================================================================
// Archivo: AuthInterceptor.kt
// Propósito: Interceptor de red que añade automáticamente el token JWT
//            a las cabeceras de las peticiones HTTP para autenticar al usuario.
// ========================================================================
package com.noskill.anymeal.data.network

import android.content.Context
import com.noskill.anymeal.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

// Interceptor para añadir automáticamente el token JWT a las cabeceras de las peticiones HTTP.
class AuthInterceptor(context: Context) : Interceptor {
    // Instancia de SessionManager para acceder al token de autenticación almacenado.
    private val sessionManager = SessionManager(context)

    // Método principal del interceptor que se ejecuta en cada petición HTTP.
    override fun intercept(chain: Interceptor.Chain): Response {
        // Crea un nuevo constructor de la petición original.
        val requestBuilder = chain.request().newBuilder()

        // Si existe un token de autenticación, lo añade a la cabecera 'Authorization'.
        sessionManager.fetchAuthToken()?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        // Continúa con la cadena de interceptores y devuelve la respuesta.
        return chain.proceed(requestBuilder.build())
    }
}