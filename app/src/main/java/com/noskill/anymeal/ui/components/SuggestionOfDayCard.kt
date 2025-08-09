/**
 * SuggestionOfDayCard.kt
 *
 * Este archivo define un componente Composable que muestra una tarjeta destacada con la
 * sugerencia de receta del día. Presenta un diseño con color de acento suave, icono representativo
 * y texto informativo para atraer la atención del usuario hacia recetas recomendadas.
 */
package com.noskill.anymeal.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiFoodBeverage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.noskill.anymeal.ui.theme.accent

/**
 * Componente que muestra una tarjeta clicable con la sugerencia de receta del día.
 * Diseñada para destacar y promover contenido recomendado con un estilo visual diferenciado
 * mediante el uso del color de acento con transparencia y un icono representativo.
 *
 * @param modifier Modificador opcional para personalizar el diseño
 * @param onClick Función callback que se ejecuta cuando el usuario hace clic en la tarjeta
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionOfDayCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit // Parámetro para la acción de clic
) {
    // Tarjeta principal con color de acento y bordes redondeados
    Card(
        onClick = onClick, // Hace que toda la tarjeta sea clicable
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.accent.copy(alpha = 0.11f) // Color de acento con alta transparencia para sutileza
        ),
        shape = RoundedCornerShape(20.dp), // Bordes muy redondeados para destacar visualmente
        modifier = modifier
            .fillMaxWidth()             // Ocupa todo el ancho disponible
            .height(68.dp)              // Altura fija para consistencia visual
            .padding(vertical = 2.dp)   // Espaciado vertical ligero
    ) {
        // Fila que contiene el icono y el texto informativo
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),   // Espaciado horizontal para el contenido
            verticalAlignment = Alignment.CenterVertically  // Centra los elementos verticalmente
        ) {
            // Icono representativo de comida/bebida
            Icon(
                imageVector = Icons.Filled.EmojiFoodBeverage,
                contentDescription = "Sugerencia",           // Descripción para accesibilidad
                tint = MaterialTheme.colorScheme.primary,    // Color primario para el icono
                modifier = Modifier.size(36.dp)              // Tamaño prominente para el icono
            )
            Spacer(Modifier.width(18.dp))   // Espacio entre el icono y el texto

            // Texto informativo sobre la sugerencia del día
            Text(
                "¿Ya viste la receta sugerida de hoy? Haz click en 'Desayuno saludable'.",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), // Estilo con peso medio para mejor legibilidad
                color = MaterialTheme.colorScheme.onSurface,  // Color estándar para texto sobre superficie
                maxLines = 2                                  // Limita el texto a dos líneas
            )
        }
    }
}
