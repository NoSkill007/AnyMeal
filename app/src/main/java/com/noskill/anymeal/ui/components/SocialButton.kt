/**
 * SocialButton.kt
 *
 * Este archivo define un componente Composable que implementa un botón circular para
 * integración con redes sociales o servicios externos. Presenta un diseño minimalista
 * con forma circular, borde sutil y adaptaciones visuales según el tema de la aplicación.
 */
package com.noskill.anymeal.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.noskill.anymeal.ui.theme.isLight

/**
 * Componente de botón social que muestra un icono dentro de un círculo con borde.
 * Utilizado para botones de integración con servicios externos como Google, Facebook, Apple, etc.
 * Se adapta visualmente al tema claro/oscuro de la aplicación.
 *
 * @param onClick Función callback que se ejecuta cuando se hace clic en el botón
 * @param iconResId ID del recurso de la imagen del icono a mostrar
 * @param contentDescription Descripción del contenido para accesibilidad
 * @param modifier Modificador opcional para personalizar el diseño
 */
@Composable
fun SocialButton(
    onClick: () -> Unit,
    iconResId: Int,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    // Determina si el tema actual es oscuro para ajustar la apariencia visual
    val isDark = !MaterialTheme.colorScheme.isLight

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.size(52.dp),        // Tamaño fijo circular
        shape = CircleShape,                    // Forma perfectamente circular
        border = BorderStroke(
            width = 1.dp,                       // Borde fino
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)  // Color de borde semi-transparente
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            // Contenedor transparente para que se vea el fondo personalizado
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)    // Sin padding interno para maximizar el área del icono
    ) {
        // Box como contenedor para aplicar el fondo y centrar el icono
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    // Adaptación del fondo según el tema
                    color = if (isDark) {
                        // En modo oscuro: fondo sutil para mejorar visibilidad y contraste
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    } else {
                        // En modo claro: sin fondo adicional
                        Color.Transparent
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center  // Centra el icono perfectamente
        ) {
            // Imagen del icono del servicio social o externo
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = contentDescription,  // Descripción para accesibilidad
                modifier = Modifier.size(24.dp)           // Tamaño fijo del icono
            )
        }
    }
}
