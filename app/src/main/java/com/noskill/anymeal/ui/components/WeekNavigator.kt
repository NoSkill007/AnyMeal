/**
 * WeekNavigator.kt
 *
 * Este archivo define un componente Composable que implementa un navegador semanal
 * con flechas de navegación para avanzar o retroceder entre semanas. Proporciona
 * una interfaz intuitiva para la navegación temporal en la planificación de comidas.
 */
package com.noskill.anymeal.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

/**
 * Componente que muestra un navegador de semanas con botones de navegación y el rango de fechas actual.
 * Permite al usuario moverse entre diferentes semanas para ver y planificar comidas por periodo.
 *
 * @param weekDateRange Texto que muestra el rango de fechas de la semana actual (ej: "5-11 Mayo 2025")
 * @param onPreviousWeek Función callback que se ejecuta cuando el usuario navega a la semana anterior
 * @param onNextWeek Función callback que se ejecuta cuando el usuario navega a la semana siguiente
 * @param modifier Modificador opcional para personalizar el diseño
 */
@Composable
fun WeekNavigator(
    weekDateRange: String,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Fila principal que contiene los botones de navegación y el texto del rango de fechas
    Row(
        modifier = modifier.fillMaxWidth(),                    // Ocupa todo el ancho disponible
        verticalAlignment = Alignment.CenterVertically,        // Alinea los elementos verticalmente al centro
        horizontalArrangement = Arrangement.SpaceBetween       // Distribuye los elementos con espacio uniforme entre ellos
    ) {
        // Botón para navegar a la semana anterior
        IconButton(onClick = onPreviousWeek) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Semana Anterior"         // Descripción para accesibilidad
            )
        }

        // Texto que muestra el rango de fechas de la semana actual
        Text(
            text = weekDateRange,
            style = MaterialTheme.typography.titleMedium,      // Estilo de título mediano según el tema
            fontWeight = FontWeight.SemiBold                   // Peso de fuente semibold para mejor legibilidad
        )

        // Botón para navegar a la semana siguiente
        IconButton(onClick = onNextWeek) {
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = "Semana Siguiente"        // Descripción para accesibilidad
            )
        }
    }
}
