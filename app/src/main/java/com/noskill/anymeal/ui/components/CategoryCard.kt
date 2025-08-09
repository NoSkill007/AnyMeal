/**
 * CategoryCard.kt
 *
 * Este archivo define un componente Composable que representa una tarjeta de categoría
 * utilizada para mostrar y seleccionar diferentes categorías de comidas o recetas en la aplicación.
 * Muestra un icono junto con el nombre de la categoría en un diseño compacto y clicable.
 */
package com.noskill.anymeal.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.noskill.anymeal.ui.theme.textSecondary

/**
 * Componente que muestra una tarjeta de categoría con un icono y un nombre.
 * Utilizado para representar y permitir la selección de diferentes categorías en la aplicación.
 *
 * @param name Nombre de la categoría que se mostrará en la tarjeta
 * @param icon Icono vectorial que representa visualmente la categoría
 * @param onClick Función callback que se ejecutará cuando el usuario pulse la tarjeta
 * @param modifier Modificador opcional para personalizar el diseño
 */
@Composable
fun CategoryCard(
    name: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Superficie clicable que actúa como contenedor principal de la tarjeta
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,       // Forma redondeada según el tema
        color = MaterialTheme.colorScheme.surface, // Color de fondo según el tema
        shadowElevation = 3.dp,                    // Sombra ligera para dar efecto de elevación
        modifier = modifier
            .width(110.dp)                         // Ancho fijo para consistencia visual
            .height(44.dp)                         // Altura fija para todas las tarjetas
    ) {
        // Fila para organizar horizontalmente el icono y el texto
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,     // Centra elementos verticalmente
            horizontalArrangement = Arrangement.Center          // Centra elementos horizontalmente
        ) {
            // Icono de la categoría
            Icon(
                imageVector = icon,
                contentDescription = name,                      // Usa el nombre como descripción para accesibilidad
                tint = MaterialTheme.colorScheme.primary,       // Color primario para destacar el icono
                modifier = Modifier.size(20.dp)                 // Tamaño consistente para todos los iconos
            )
            // Espacio entre el icono y el texto
            Spacer(Modifier.width(7.dp))
            // Texto con el nombre de la categoría
            Text(
                name,
                style = MaterialTheme.typography.bodyMedium,    // Estilo de texto según el tema
                color = MaterialTheme.colorScheme.textSecondary // Color secundario para el texto
            )
        }
    }
}