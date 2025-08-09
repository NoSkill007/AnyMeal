/**
 * SocialLoginRow.kt
 *
 * Este archivo define un componente Composable que implementa una fila horizontal de botones
 * para inicio de sesión con servicios externos (Google, Facebook, Apple). Proporciona una
 * interfaz visual unificada para las opciones de autenticación social dentro de la aplicación.
 */
package com.noskill.anymeal.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.noskill.anymeal.R // Asegúrate de tener estos recursos

/**
 * Componente que muestra una fila horizontal de botones para iniciar sesión con diferentes
 * plataformas sociales. Organiza los botones SocialButton en una fila con espaciado uniforme
 * y centrado horizontal.
 *
 * @param onGoogleClick Función callback que se ejecuta cuando se hace clic en el botón de Google
 * @param onFacebookClick Función callback que se ejecuta cuando se hace clic en el botón de Facebook
 * @param onAppleClick Función callback que se ejecuta cuando se hace clic en el botón de Apple
 * @param modifier Modificador opcional para personalizar el diseño
 */
@Composable
fun SocialLoginRow(
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit,
    onAppleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Fila horizontal que contiene los botones sociales, centrada y con espaciado uniforme
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally) // Espacio de 20dp entre botones, centrados horizontalmente
    ) {
        // Botón para inicio de sesión con Google
        SocialButton(
            onClick = onGoogleClick,
            iconResId = R.drawable.ic_google,
            contentDescription = "Login with Google"  // Descripción para accesibilidad
        )
        // Botón para inicio de sesión con Facebook
        SocialButton(
            onClick = onFacebookClick,
            iconResId = R.drawable.ic_facebook,
            contentDescription = "Login with Facebook"  // Descripción para accesibilidad
        )
        // Botón para inicio de sesión con Apple
        SocialButton(
            onClick = onAppleClick,
            iconResId = R.drawable.ic_apple,
            contentDescription = "Login with Apple"  // Descripción para accesibilidad
        )
    }
}
