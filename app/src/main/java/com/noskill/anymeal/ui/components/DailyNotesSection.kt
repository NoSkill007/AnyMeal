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

@Composable
fun DailyNotesSection(
    notes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var internalText by remember(notes) { mutableStateOf(notes) }

    LaunchedEffect(internalText) {
        if (internalText != notes) {
            delay(500L)
            onNotesChange(internalText)
        }
    }

    // CORRECCIÓN DE DISEÑO: Se ajustan los colores para una mejor integración visual.
    Card(
        modifier = modifier.fillMaxWidth(),
        // Usamos `surfaceContainer` que es el color estándar para este tipo de contenedores,
        // asegurando que coincida con las otras tarjetas de la pantalla.
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp), // Aumentamos el espacio
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.EditNote,
                    contentDescription = "Notas del día",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Notas del Día",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
            }
            // Usamos un TextField básico para una máxima integración, en lugar de OutlinedTextField.
            TextField(
                value = internalText,
                onValueChange = { internalText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 100.dp),
                placeholder = { Text("Añade un recordatorio o detalle...") },
                // CORRECCIÓN DE DISEÑO: Colores para integrar el campo de texto.
                colors = TextFieldDefaults.colors(
                    // Hacemos el fondo del campo de texto transparente para que tome el color de la Card.
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    // Color del cursor
                    cursorColor = MaterialTheme.colorScheme.primary,
                    // Color de la línea indicadora
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Transparent, // Sin línea cuando no está enfocado
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.medium
            )
        }
    }
}
