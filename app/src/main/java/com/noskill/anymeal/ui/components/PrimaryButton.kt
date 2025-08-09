/**
 * PrimaryButton.kt
 *
 * Este archivo define un componente Composable que implementa un botón primario personalizado
 * para la aplicación. Proporciona un diseño consistente con la identidad visual de la app,
 * incorporando estados de carga, deshabilitado y normal con los colores del tema.
 */
package com.noskill.anymeal.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Componente de botón primario que se utiliza para acciones principales en la aplicación.
 * Incluye soporte para mostrar un indicador de carga, estados habilitado/deshabilitado
 * y se adapta automáticamente al tema de la aplicación.
 *
 * @param text Texto a mostrar en el botón
 * @param onClick Función callback que se ejecuta cuando se hace clic en el botón
 * @param modifier Modificador opcional para personalizar el diseño
 * @param enabled Estado que determina si el botón está habilitado o no
 * @param isLoading Estado que determina si se debe mostrar un indicador de carga
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()       // Ocupa todo el ancho disponible
            .height(52.dp),       // Altura fija para consistencia visual
        shape = RoundedCornerShape(16.dp),  // Bordes redondeados para concordar con el diseño de la app
        enabled = enabled && !isLoading,     // Deshabilita el botón cuando está cargando o explícitamente deshabilitado
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,        // Color de fondo según el tema
            contentColor = MaterialTheme.colorScheme.onPrimary,        // Color de contenido según el tema
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)  // Versión semitransparente para estado deshabilitado
        )
    ) {
        // Muestra un indicador de progreso circular cuando está en estado de carga
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),              // Tamaño consistente con el texto
                color = MaterialTheme.colorScheme.onPrimary,  // Color que contrasta con el fondo del botón
                strokeWidth = 3.dp                            // Grosor de la línea del indicador
            )
        } else {
            // Muestra el texto del botón cuando no está cargando
            Text(text, style = MaterialTheme.typography.titleMedium)  // Estilo de texto según el tema
        }
    }
}
