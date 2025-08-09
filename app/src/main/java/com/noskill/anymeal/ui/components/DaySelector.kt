/**
 * DaySelector.kt
 *
 * Este archivo define un componente Composable que implementa un selector horizontal de días.
 * Permite al usuario seleccionar un día específico de una lista, destacando visualmente
 * el día seleccionado. El componente está diseñado para usarse en calendarios o planificadores
 * de comidas donde la navegación entre días es necesaria.
 */
package com.noskill.anymeal.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.noskill.anymeal.ui.theme.isLight

/**
 * Componente de selección de días que muestra una fila horizontal desplazable de botones de días.
 * Resalta visualmente el día actualmente seleccionado y permite la navegación entre días.
 *
 * @param days Lista de strings que representan los días a mostrar (ej: "Lun", "Mar", etc.)
 * @param selectedDayIndex Índice del día actualmente seleccionado en la lista de días
 * @param onDaySelected Función callback que se ejecuta cuando el usuario selecciona un día, recibe el índice seleccionado
 * @param modifier Modificador opcional para personalizar el diseño
 * @param lazyListState Estado de la lista perezosa que permite control externo del desplazamiento
 */
@Composable
fun DaySelector(
    days: List<String>,
    selectedDayIndex: Int,
    onDaySelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState() // Acepta un estado para ser controlado
) {
    // Determina si el tema actual es oscuro para ajustar el contraste visual
    val isDark = !MaterialTheme.colorScheme.isLight

    // Fila horizontal con desplazamiento que muestra los botones de días
    LazyRow(
        state = lazyListState, // Usa el estado proporcionado
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp), // Espacio horizontal entre botones
        contentPadding = PaddingValues(horizontal = 16.dp)   // Relleno en los extremos de la fila
    ) {
        // Genera un botón para cada día en la lista
        itemsIndexed(days) { index, day ->
            val isSelected = selectedDayIndex == index
            // Botón con contorno que representa cada día
            OutlinedButton(
                onClick = { onDaySelected(index) },
                shape = RoundedCornerShape(16.dp), // Forma redondeada para los botones
                // Borde más grueso y destacado para el día seleccionado
                border = if (isSelected)
                    BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                else
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                // Colores adaptados según el estado de selección y el tema
                colors = ButtonDefaults.outlinedButtonColors(
                    // Fondo con color primario semi-transparente para el día seleccionado
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primary.copy(alpha = if (isDark) 0.15f else 0.1f)
                    else
                        MaterialTheme.colorScheme.surface,
                    // Color de texto más destacado para el día seleccionado
                    contentColor = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            ) {
                // Texto del día con negrita si está seleccionado
                Text(
                    text = day,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
