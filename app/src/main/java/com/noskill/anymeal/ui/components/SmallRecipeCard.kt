/* --------------------------------------------------------------------
 * Archivo: SmallRecipeCard.kt (REDESIGN FINAL PARA SINTONÍA)
 * Propósito: Rediseño visual para un aspecto más moderno y estético,
 * sincronizando con el diseño limpio y minimalista de HomeScreen.
 * Se mantiene el tamaño fijo y la lógica de ContentScale.Crop.
 * --------------------------------------------------------------------
 */
package com.noskill.anymeal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.* // Asegurarse de importar AnimatedVisibility, si se usa
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow // Importar TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.noskill.anymeal.ui.models.RecipePreviewUi
import com.noskill.anymeal.viewmodel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallRecipeCard(
    recipe: RecipePreviewUi,
    onClick: () -> Unit,
    favoritesViewModel: FavoritesViewModel,
    modifier: Modifier = Modifier // Se mantiene el nombre 'modifier'
) {
    val favoriteIds by favoritesViewModel.favoriteRecipeIds.collectAsState()
    val isFavorite = recipe.id in favoriteIds

    Card( // <-- CAMBIO CLAVE: Volver a usar Card regular (menos sombra)
        onClick = onClick,
        modifier = Modifier
            .width(412.dp) // Ancho fijo
            .height(260.dp) // Alto fijo
            .then(modifier), // Aplicar el modificador externo después del tamaño fijo
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors( // Usar colores de Card regular
            containerColor = MaterialTheme.colorScheme.surface // <-- Color de superficie más plano
        ),
        // Puedes añadir un borde sutil para definición si es necesario, o quitar la elevación
        // border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        // Se elimina la elevación explícita si se usa Card regular, la sombra es por defecto.
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // <-- Elevación sutil
    ) {
        Box(modifier = Modifier.fillMaxSize()) { // La Box llena la tarjeta
            Column(modifier = Modifier.fillMaxSize()) { // La columna interna llena la Box
                AsyncImage(
                    model = recipe.imageUrl,
                    contentDescription = recipe.title,
                    contentScale = ContentScale.Crop, // La imagen se recorta para llenar el espacio asignado
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp) // Altura fija para el área de la imagen
                        .background(MaterialTheme.colorScheme.surfaceDim) // Fondo para rellenar si la imagen es muy estrecha o no carga
                )
                // Contenido de texto
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp) // Ajuste de padding
                        .weight(1f), // Dar peso para que ocupe el espacio restante en la columna
                    verticalArrangement = Arrangement.Center // Centrar el contenido verticalmente
                ) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.titleLarge, // Título prominente
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp)) // Espacio entre título y detalles
                    Text(
                        text = "${recipe.time} · ${recipe.difficulty}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            // Botón de favorito superpuesto
            IconButton( // <-- Cambio a IconButton directamente para un estilo más limpio
                onClick = { favoritesViewModel.toggleFavorite(recipe.id) },
                modifier = Modifier
                    .align(Alignment.TopEnd) // Alineado a la esquina superior derecha
                    .padding(8.dp) // Padding desde los bordes
                    .clip(CircleShape) // Recortar en círculo
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)) // Fondo semitransparente con color de superficie
                    .size(40.dp) // Tamaño del botón
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface // Color del icono
                )
            }
        }
    }
}