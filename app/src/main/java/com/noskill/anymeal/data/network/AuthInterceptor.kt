// ========================================================================
// Archivo: data/network/AuthInterceptor.kt
// ========================================================================
package com.noskill.anymeal.data.network

import android.content.Context
import com.noskill.anymeal.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

// Interceptor para añadir automáticamente el token JWT a las cabeceras.
class AuthInterceptor(context: Context) : Interceptor {
    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        sessionManager.fetchAuthToken()?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}