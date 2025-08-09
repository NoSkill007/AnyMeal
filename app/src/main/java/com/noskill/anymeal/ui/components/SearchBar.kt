/**
 * SearchBar.kt
 *
 * Este archivo define un componente Composable que implementa una barra de búsqueda personalizada
 * con forma de píldora, diseño moderno y efectos visuales adaptados al tema de la aplicación.
 * Proporciona un campo de texto con icono de búsqueda y animación de etiqueta.
 */
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

/**
 * Componente de barra de búsqueda que permite a los usuarios buscar recetas e ingredientes.
 * Presenta un diseño moderno con forma de píldora y colores adaptados al tema de la aplicación.
 *
 * @param value Texto actual en el campo de búsqueda
 * @param onValueChange Función callback que se ejecuta cuando cambia el texto, recibe el nuevo texto
 * @param modifier Modificador opcional para personalizar el diseño
 */
@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Determina si el tema actual es oscuro para ajustar la transparencia
    val isDark = !MaterialTheme.colorScheme.isLight

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(), // Ocupa todo el ancho disponible
        // Usa 'label' en lugar de 'placeholder' para aprovechar la animación cuando hay texto
        label = { Text("Buscar recetas, ingredientes…") },
        // Icono de búsqueda al inicio del campo
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon" // Descripción para accesibilidad
            )
        },
        // Forma de píldora con bordes muy redondeados
        shape = RoundedCornerShape(50),
        // Personalización detallada de colores según el estado y el tema
        colors = OutlinedTextFieldDefaults.colors(
            // Color del contenedor con ajuste de transparencia según el tema
            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = if (isDark) 0.8f else 1f),
            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = if (isDark) 0.8f else 1f),
            // Manejo de bordes: invisible cuando no está enfocado, color primario cuando está enfocado
            unfocusedBorderColor = Color.Transparent, // Sin borde visible en estado normal
            focusedBorderColor = MaterialTheme.colorScheme.primary, // Borde de color al hacer clic
            // Color del cursor con el color primario del tema
            cursorColor = MaterialTheme.colorScheme.primary,
            // Colores del icono destacando el color primario
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            // Colores de la etiqueta: secundario cuando no está enfocado, primario cuando lo está
            unfocusedLabelColor = MaterialTheme.colorScheme.textSecondary,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        ),
        singleLine = true // Restringe la entrada a una sola línea
    )
}