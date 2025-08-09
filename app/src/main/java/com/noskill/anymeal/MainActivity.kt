/* --------------------------------------------------------------------
 * Archivo: MainActivity.kt
 * Propósito: Define la actividad principal de la aplicación AnyMeal.
 *            Inicializa la interfaz de usuario y configura el grafo de navegación
 *            principal, el tema (claro/oscuro) y el manejo de cierre de sesión.
 * --------------------------------------------------------------------*/

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


// Clase principal de la aplicación que extiende ComponentActivity.
class MainActivity : ComponentActivity() {
    // Método que se ejecuta al crear la actividad.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Obtiene el estado del tema del sistema (oscuro o claro).
            val systemIsDark = isSystemInDarkTheme()
            // Variable mutable para controlar el tema de la app.
            var isDarkTheme by remember { mutableStateOf(systemIsDark) }

            // Aplica el tema seleccionado a la app.
            AnyMealTheme(darkTheme = isDarkTheme) {
                // Inicializa el controlador de navegación.
                val navController = rememberNavController()

                // Configura el grafo de navegación principal y los callbacks globales.
                AppNavGraph(
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onThemeChange = { newThemeState ->
                        isDarkTheme = newThemeState // Cambia el tema cuando el usuario lo solicita.
                    },
                    // Callback para manejar el cierre de sesión.
                    onLogoutConfirmed = {
                        navController.navigate(Screen.Auth.route) {
                            // Limpia el historial de navegación para evitar volver atrás.
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
