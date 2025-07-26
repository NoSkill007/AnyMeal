// ========================================================================
// Archivo: di/NetworkModule.kt
// Propósito: Configurar y proveer la instancia de Retrofit para toda la app.
// ========================================================================
package com.noskill.anymeal.di

import android.content.Context
import com.noskill.anymeal.data.network.AuthInterceptor
import com.noskill.anymeal.data.network.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    /**
     * Dirección base del backend.
     * Esta debe ser la dirección IPv4 de tu computadora en la red local (Wi-Fi).
     * El puerto :8080 es donde se ejecuta el servidor de Spring Boot.
     */
    private const val BASE_URL = "http://192.168.151.245:8080/"

    /**
     * Crea y configura la instancia de ApiService que se usará para hacer las llamadas de red.
     * @param context El contexto de la aplicación, necesario para el SessionManager en el interceptor.
     * @return Una instancia de ApiService lista para usar.
     */
    fun provideApiService(context: Context): ApiService {
        // Interceptor para ver los logs de las peticiones en el Logcat. Muy útil para depurar.
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Cliente OkHttp personalizado.
        val client = OkHttpClient.Builder()
            .addInterceptor(logging) // Añade el interceptor de logging.
            .addInterceptor(AuthInterceptor(context)) // Añade nuestro interceptor para el token JWT.
            .build()

        // Construcción de la instancia de Retrofit.
        return Retrofit.Builder()
            .baseUrl(BASE_URL) // Establece la URL base.
            .client(client) // Usa nuestro cliente OkHttp personalizado.
            .addConverterFactory(GsonConverterFactory.create()) // Usa Gson para convertir JSON a objetos Kotlin.
            .build()
            .create(ApiService::class.java) // Crea la implementación de nuestra interfaz ApiService.
    }
}