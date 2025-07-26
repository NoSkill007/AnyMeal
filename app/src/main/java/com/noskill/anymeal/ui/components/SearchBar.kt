package com.noskill.anymeal.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.noskill.anymeal.ui.theme.isLight
import com.noskill.anymeal.ui.theme.textSecondary

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = !MaterialTheme.colorScheme.isLight

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        // Usamos 'label' en lugar de 'placeholder' para la animación
        label = { Text("Buscar recetas, ingredientes…") },
        // Ícono al inicio
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        // Forma de píldora
        shape = RoundedCornerShape(50),
        // Personalización de colores para un look moderno
        colors = OutlinedTextFieldDefaults.colors(
            // Color del contenedor
            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = if (isDark) 0.8f else 1f),
            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = if (isDark) 0.8f else 1f),
            // Color del borde
            unfocusedBorderColor = Color.Transparent, // <-- Sin borde cuando no está enfocado
            focusedBorderColor = MaterialTheme.colorScheme.primary, // Borde de color al hacer clic
            // Color del cursor
            cursorColor = MaterialTheme.colorScheme.primary,
            // Colores del ícono
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            // Colores de la etiqueta
            unfocusedLabelColor = MaterialTheme.colorScheme.textSecondary,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        ),
        singleLine = true
    )
}