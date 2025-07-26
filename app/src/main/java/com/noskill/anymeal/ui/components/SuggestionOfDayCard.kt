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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionOfDayCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit // Parámetro añadido para la acción de clic
) {
    Card(
        onClick = onClick, // Hacemos que toda la tarjeta sea clickeable
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.accent.copy(alpha = 0.11f)
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.EmojiFoodBeverage,
                contentDescription = "Sugerencia",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(Modifier.width(18.dp))
            Text(
                "¿Ya viste la receta sugerida de hoy? Haz click en 'Desayuno saludable'.",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )
        }
    }
}
