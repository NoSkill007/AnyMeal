/**
 * AuthScreen.kt
 *
 * Propósito: Define la pantalla inicial de autenticación de la aplicación AnyMeal.
 * Presenta una interfaz atractiva con animaciones de entrada para dar la bienvenida
 * a los usuarios, mostrando opciones para iniciar sesión o crear una nueva cuenta.
 * Sirve como punto de entrada principal para usuarios no autenticados.
 */
package com.noskill.anymeal.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noskill.anymeal.R // Asegúrate de tener un recurso drawable
import com.noskill.anymeal.ui.components.PrimaryButton
import com.noskill.anymeal.ui.components.SecondaryButton
import com.noskill.anymeal.ui.theme.isLight
import kotlinx.coroutines.delay

/**
 * Composable principal que define la pantalla de autenticación.
 * Muestra una imagen ilustrativa, un mensaje de bienvenida y botones para
 * iniciar sesión o registrarse, todo con animaciones de entrada para mejorar
 * la experiencia de usuario.
 *
 * @param onLoginClick Función de callback que se ejecuta cuando el usuario presiona el botón de inicio de sesión
 * @param onRegisterClick Función de callback que se ejecuta cuando el usuario presiona el botón de registro
 */
@Composable
fun AuthScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    // Control de estado para las animaciones de entrada
    var isVisible by remember { mutableStateOf(false) }

    // Efecto lanzado al componer la pantalla que activa las animaciones con un pequeño retraso
    LaunchedEffect(key1 = Unit) {
        delay(200)
        isVisible = true
    }

    // Contenedor principal de toda la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Usamos un color de fondo sólido que respeta el tema
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animación de entrada para la sección superior (imagen y textos)
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it / 2 },
                    animationSpec = tween(durationMillis = 800, delayMillis = 100)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 100))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 48.dp)
                ) {
                    // Imagen ilustrativa de cocina
                    Image(
                        painter = painterResource(id = R.drawable.cooking_illustration),
                        contentDescription = "Ilustración de Cocina",
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .aspectRatio(1f)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    // Título principal de bienvenida
                    Text(
                        text = "Bienvenido a AnyMeal",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    // Subtítulo descriptivo de la aplicación
                    Text(
                        text = "Encuentra y guarda tus recetas favoritas en un solo lugar.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Espacio flexible que empuja los botones hacia la parte inferior
            Spacer(modifier = Modifier.weight(1f))

            // Animación de entrada para la sección inferior (botones)
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(durationMillis = 800, delayMillis = 300)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 300))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    // Botón principal para iniciar sesión
                    PrimaryButton(
                        text = "Iniciar Sesión",
                        onClick = onLoginClick
                    )
                    // Botón secundario para crear una nueva cuenta
                    SecondaryButton(
                        text = "Crear Cuenta",
                        onClick = onRegisterClick
                    )
                }
            }
        }
    }
}
