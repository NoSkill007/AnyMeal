/*
Archivo: NetworkModule.kt
Propósito: Configura y provee la instancia de Retrofit y OkHttpClient para la comunicación con el backend. Permite inyectar ApiService en la app usando dependencias.
*/
package com.noskill.anymeal.data.di

import android.content.Context // Importa Context para acceder a recursos de la app.
import com.noskill.anymeal.data.network.AuthInterceptor // Importa el interceptor de autenticación personalizado.
import com.noskill.anymeal.data.network.ApiService // Importa la interfaz de servicio de la API.
import okhttp3.OkHttpClient // Importa OkHttpClient para gestionar las peticiones HTTP.
import okhttp3.logging.HttpLoggingInterceptor // Importa el interceptor para loguear las peticiones HTTP.
import retrofit2.Retrofit // Importa Retrofit para la comunicación con APIs REST.
import retrofit2.converter.gson.GsonConverterFactory // Importa el convertidor Gson para manejar JSON.

object NetworkModule { // Define un objeto singleton para proveer dependencias de red.

    /**
     * Dirección base del backend.
     * Esta debe ser la dirección IPv4 de tu computadora en la red local (Wi-Fi).
     * El puerto :8080 es donde se ejecuta el servidor de Spring Boot.
     */
    private const val BASE_URL = "http://192.168.68.51:8080/" // URL base del backend donde se reciben las peticiones.

    /**
     * Crea y configura la instancia de ApiService que se usará para hacer las llamadas de red.
     * @param context El contexto de la aplicación, necesario para el SessionManager en el interceptor.
     * @return Una instancia de ApiService lista para usar.
     */
    fun provideApiService(context: Context): ApiService { // Provee una instancia de ApiService configurada.
        // Interceptor para ver los logs de las peticiones en el Logcat. Muy útil para depurar.
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Configura el nivel de log para mostrar cuerpo de las peticiones/respuestas.
        }

        // Cliente OkHttp personalizado.
        val client = OkHttpClient.Builder()
            .addInterceptor(logging) // Agrega el interceptor de logging para depuración.
            .addInterceptor(AuthInterceptor(context)) // Agrega el interceptor de autenticación para añadir el token JWT.
            .build() // Construye el cliente OkHttp personalizado.

        // Construcción de la instancia de Retrofit.
        return Retrofit.Builder()
            .baseUrl(BASE_URL) // Establece la URL base para las peticiones.
            .client(client) // Usa el cliente OkHttp configurado.
            .addConverterFactory(GsonConverterFactory.create()) // Usa Gson para convertir JSON a objetos Kotlin.
            .build()
            .create(ApiService::class.java) // Crea la implementación de la interfaz ApiService.
    }
}