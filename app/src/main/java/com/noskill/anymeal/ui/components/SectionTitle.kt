/**
 * SectionTitle.kt
 *
 * Este archivo define un componente Composable que implementa un título de sección estándar
 * para la aplicación. Proporciona un estilo visual consistente para los encabezados de secciones
 * en las diferentes pantallas, usando el color de acento y el peso de fuente semibold.
 */
package com.noskill.anymeal.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.noskill.anymeal.ui.theme.accent

/**
 * Componente que muestra un título de sección con el estilo visual definido para la aplicación.
 * Utiliza el color de acento del tema y presenta un espaciado vertical consistente.
 *
 * @param text Texto a mostrar como título de la sección
 * @param modifier Modificador opcional para personalizar el diseño
 */
@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        // Aplica el estilo de título mediano con peso de fuente semibold para destacar visualmente
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        // Utiliza el color de acento del tema para diferenciar los títulos de sección
        color = MaterialTheme.colorScheme.accent,
        modifier = modifier
            .fillMaxWidth()              // Ocupa todo el ancho disponible
            .padding(vertical = 12.dp,   // Espaciado vertical consistente para todas las secciones
                    horizontal = 2.dp)   // Ligero espaciado horizontal
    )
}