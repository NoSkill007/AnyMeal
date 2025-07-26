// ========================================================================
// Archivo: ui/screens/SplashScreen.kt
// ========================================================================
package com.noskill.anymeal.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.noskill.anymeal.data.local.SessionManager
import com.noskill.anymeal.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    LaunchedEffect(key1 = true) {
        // Pequeña demora para que la pantalla de carga sea visible
        delay(1500)

        // Comprobamos si existe un token
        val token = sessionManager.fetchAuthToken()

        // Definimos la ruta de destino
        val destination = if (token.isNullOrBlank()) {
            Screen.Auth.route
        } else {
            Screen.Home.route
        }

        // Navegamos a la pantalla correspondiente, limpiando la pila de navegación
        // para que el usuario no pueda volver a la SplashScreen.
        navController.navigate(destination) {
            popUpTo(Screen.Splash.route) {
                inclusive = true
            }
        }
    }

    // UI simple de la pantalla de carga
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}