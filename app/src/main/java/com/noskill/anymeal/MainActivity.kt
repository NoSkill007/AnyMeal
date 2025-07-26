package com.noskill.anymeal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.noskill.anymeal.navigation.AppNavGraph
import com.noskill.anymeal.navigation.Screen
import com.noskill.anymeal.ui.theme.AnyMealTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemIsDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemIsDark) }

            AnyMealTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                AppNavGraph(
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onThemeChange = { newThemeState ->
                        isDarkTheme = newThemeState
                    },
                    // CORRECCIÓN: Se añade la función para manejar el cierre de sesión.
                    onLogoutConfirmed = {
                        navController.navigate(Screen.Auth.route) {
                            // Limpia todo el historial de navegación para que el usuario
                            // no pueda volver a las pantallas anteriores.
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}
