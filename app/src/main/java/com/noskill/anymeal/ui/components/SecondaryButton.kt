/**
 * SecondaryButton.kt
 *
 * Este archivo define un componente Composable que implementa un botón secundario con borde
 * para la aplicación. Proporciona una alternativa visual al botón primario, utilizando un diseño
 * con contorno y fondo transparente (o ligeramente coloreado en modo oscuro), manteniendo
 * la consistencia con la identidad visual de la aplicación.
 */
package com.noskill.anymeal.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.noskill.anymeal.ui.theme.isLight

/**
 * Componente de botón secundario que se utiliza para acciones alternativas o menos destacadas.
 * Presenta un diseño con borde y fondo transparente (o sutilmente coloreado en modo oscuro),
 * manteniendo la altura y forma consistentes con otros botones de la aplicación.
 *
 * @param text Texto a mostrar en el botón
 * @param onClick Función callback que se ejecuta cuando se hace clic en el botón
 * @param modifier Modificador opcional para personalizar el diseño
 * @param enabled Estado que determina si el botón está habilitado o no
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    // Determina si el tema actual es oscuro para ajustar la visibilidad del botón
    val isDark = !MaterialTheme.colorScheme.isLight

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()       // Ocupa todo el ancho disponible
            .height(52.dp),       // Altura fija para consistencia con otros botones
        shape = RoundedCornerShape(16.dp),  // Bordes redondeados para concordar con el diseño de la app
        enabled = enabled,        // Aplica el estado habilitado/deshabilitado
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),  // Borde con color primario
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,  // Color del texto igual al color primario
            // Ajuste del fondo según el tema: transparente en modo claro, ligeramente coloreado en modo oscuro
            containerColor = if (isDark)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) // Fondo sutil en modo oscuro para mejorar visibilidad
            else
                Color.Transparent // Completamente transparente en modo claro
        )
    ) {
        // Texto del botón con estilo consistente
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}
