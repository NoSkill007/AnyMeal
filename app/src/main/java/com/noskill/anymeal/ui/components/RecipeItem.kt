/**
 * RecipeItem.kt
 *
 * Este archivo define un componente Composable que muestra un elemento individual de receta
 * dentro de una lista o sección expandible. Representa cada receta con su imagen, título,
 * tiempo de preparación, dificultad y botones para marcar como favorito o eliminar.
 */
package com.noskill.anymeal.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.noskill.anymeal.ui.models.PlanEntry

/**
 * Componente que muestra una receta individual como elemento de una lista o sección.
 * Incluye imagen, información básica de la receta y acciones como marcar favorito o eliminar.
 *
 * @param entry Objeto PlanEntry que contiene la información de la receta y su ID en el backend
 * @param onClick Función callback que se ejecuta cuando se hace clic en la receta, recibe el ID de la receta
 * @param onDelete Función callback que se ejecuta cuando se elimina la receta, recibe el ID del backend
 * @param modifier Modificador opcional para personalizar el diseño
 * @param isFavorite Estado que indica si la receta está marcada como favorita
 * @param onFavoriteClick Función callback que se ejecuta cuando se marca/desmarca como favorita
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeItem(
    entry: PlanEntry,
    onClick: (Int) -> Unit,
    onDelete: (Long) -> Unit, // Acepta el ID del backend (Long)
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    // Extraer el objeto recipe del entry para mejor legibilidad
    val recipe = entry.recipe

    // Tarjeta contenedora para toda la receta
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),  // Bordes redondeados para estética consistente
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow // Color de fondo sutil
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp) // Elevación ligera para efecto visual
    ) {
        // Fila principal que contiene todos los elementos de la receta
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(recipe.id.toInt()) } // Hace clicable toda la fila
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically // Alinea los elementos verticalmente
        ) {
            // Imagen de la receta cargada de forma asíncrona
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = recipe.title, // Usa el título como descripción para accesibilidad
                contentScale = ContentScale.Crop, // Recorta la imagen para llenar el espacio
                modifier = Modifier
                    .size(56.dp) // Tamaño fijo para la imagen
                    .clip(RoundedCornerShape(12.dp)) // Bordes redondeados para la imagen
            )
            Spacer(modifier = Modifier.width(16.dp)) // Espacio entre la imagen y el texto

            // Columna con información textual de la receta
            Column(modifier = Modifier.weight(1f)) { // Ocupa el espacio disponible
                // Título de la receta en negrita
                Text(
                    text = recipe.title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1, // Limita a una línea
                    overflow = TextOverflow.Ellipsis // Muestra elipsis si el texto es muy largo
                )
                // Información secundaria: tiempo y dificultad
                Text(
                    text = "${recipe.time} · ${recipe.difficulty}",
                    style = MaterialTheme.typography.bodySmall, // Texto más pequeño
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Color secundario
                )
            }

            // Fila de botones de acción a la derecha
            Row {
                // Botón para marcar/desmarcar como favorito
                IconToggleButton(
                    checked = isFavorite,
                    onCheckedChange = { onFavoriteClick() }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Botón para eliminar la receta del plan
                IconButton(onClick = { onDelete(entry.backendId) }) { // Usa el ID del backend
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Eliminar receta",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) // Color semitransparente
                    )
                }
            }
        }
    }
}