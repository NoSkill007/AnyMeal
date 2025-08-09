/**
 * DailyNotesSection.kt
 *
 * Este archivo define un componente Composable que implementa una sección para que los usuarios
 * puedan agregar notas diarias en la aplicación. Proporciona un campo de texto con auto-guardado
 * para que los usuarios puedan registrar recordatorios o detalles relacionados con sus comidas
 * o planes alimenticios del día.
 */
package com.noskill.anymeal.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Componente que muestra una sección para escribir y gestionar notas diarias.
 * Incluye un título con icono y un campo de texto que se actualiza automáticamente
 * después de un breve retraso para evitar actualizaciones excesivas durante la escritura.
 *
 * @param notes Texto actual de las notas que se mostrará en el campo de texto
 * @param onNotesChange Función callback que se ejecuta cuando cambia el contenido de las notas
 * @param modifier Modificador opcional para personalizar el diseño
 */
@Composable
fun DailyNotesSection(
    notes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Estado interno para manejar el texto durante la edición, inicializado con las notas actuales
    var internalText by remember(notes) { mutableStateOf(notes) }

    // Efecto que retrasa la propagación de cambios para evitar llamadas excesivas durante la escritura
    LaunchedEffect(internalText) {
        if (internalText != notes) {
            delay(500L)  // Espera 500ms después del último cambio antes de notificar
            onNotesChange(internalText)
        }
    }

    // Tarjeta contenedora para la sección de notas
    Card(
        modifier = modifier.fillMaxWidth(),
        // Uso de colores estándar para contenedores según Material Design 3
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila superior con icono y título
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                // Icono de notas
                Icon(
                    imageVector = Icons.Outlined.EditNote,
                    contentDescription = "Notas del día",
                    tint = MaterialTheme.colorScheme.primary
                )
                // Título de la sección
                Text(
                    text = "Notas del Día",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
            }

            // Campo de texto para introducir las notas
            TextField(
                value = internalText,
                onValueChange = { internalText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 100.dp),  // Altura mínima para facilitar la escritura
                placeholder = { Text("Añade un recordatorio o detalle...") },
                // Personalización de colores para integrar visualmente con la tarjeta
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,      // Fondo transparente
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary, // Color del cursor
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary, // Línea inferior cuando está enfocado
                    unfocusedIndicatorColor = Color.Transparent,    // Sin línea cuando no está enfocado
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.medium  // Forma redondeada según el tema
            )
        }
    }
}
