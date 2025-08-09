/**
 * SmallRecipeCard.kt
 *
 * Este archivo define un componente Composable que implementa una tarjeta de receta de tamaño pequeño,
 * diseñada para mostrar una vista previa de recetas en listas horizontales o cuadrículas.
 * Presenta la imagen de la receta, título, tiempo de preparación, dificultad y un botón para
 * marcarla como favorita con un diseño moderno y estético.
 */
package com.noskill.anymeal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.noskill.anymeal.ui.models.RecipePreviewUi
import com.noskill.anymeal.viewmodel.FavoritesViewModel

/**
 * Componente que muestra una tarjeta de receta de tamaño pequeño con imagen destacada.
 * Incluye la imagen de la receta, título, tiempo de preparación, dificultad y un botón
 * para marcar/desmarcar como favorita. Diseñado con dimensiones fijas para mantener
 * consistencia visual en listados.
 *
 * @param recipe Objeto RecipePreviewUi que contiene la información de la receta a mostrar
 * @param onClick Función callback que se ejecuta cuando se hace clic en la tarjeta
 * @param favoritesViewModel ViewModel que gestiona el estado de recetas favoritas
 * @param modifier Modificador opcional para personalizar el diseño
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallRecipeCard(
    recipe: RecipePreviewUi,
    onClick: () -> Unit,
    favoritesViewModel: FavoritesViewModel,
    modifier: Modifier = Modifier
) {
    // Obtiene los IDs de recetas favoritas como estado observable
    val favoriteIds by favoritesViewModel.favoriteRecipeIds.collectAsState()
    // Determina si esta receta está marcada como favorita
    val isFavorite = recipe.id in favoriteIds

    // Tarjeta contenedora principal con diseño limpio y elevación sutil
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(412.dp)        // Ancho fijo para consistencia visual
            .height(260.dp)       // Alto fijo para consistencia visual
            .then(modifier),      // Aplica el modificador externo después del tamaño fijo
        shape = RoundedCornerShape(16.dp),  // Bordes redondeados para estética moderna
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface  // Color de superficie plano para estilo minimalista
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)  // Elevación sutil para efecto visual
    ) {
        // Box como contenedor principal para permitir superposición de elementos
        Box(modifier = Modifier.fillMaxSize()) {
            // Columna para organizar verticalmente la imagen y el texto
            Column(modifier = Modifier.fillMaxSize()) {
                // Imagen de la receta cargada de forma asíncrona
                AsyncImage(
                    model = recipe.imageUrl,
                    contentDescription = recipe.title,  // Título como descripción para accesibilidad
                    contentScale = ContentScale.Crop,   // Recorta la imagen para llenar el espacio asignado
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)  // Altura fija para el área de la imagen
                        .background(MaterialTheme.colorScheme.surfaceDim)  // Fondo si la imagen no carga o es muy estrecha
                )

                // Contenido textual: título y detalles de la receta
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .weight(1f),  // Ocupa el espacio restante en la columna
                    verticalArrangement = Arrangement.Center  // Centra el contenido verticalmente
                ) {
                    // Título de la receta en negrita con elipsis si es muy largo
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))  // Espacio entre título y detalles

                    // Información secundaria: tiempo de preparación y dificultad
                    Text(
                        text = "${recipe.time} · ${recipe.difficulty}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Botón de favorito superpuesto en la esquina superior derecha
            IconButton(
                onClick = { favoritesViewModel.toggleFavorite(recipe.id) },  // Alterna estado de favorito
                modifier = Modifier
                    .align(Alignment.TopEnd)  // Alineado a la esquina superior derecha
                    .padding(8.dp)            // Espaciado desde los bordes
                    .clip(CircleShape)        // Forma circular
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))  // Fondo semitransparente
                    .size(40.dp)              // Tamaño fijo del botón
            ) {
                // Icono que cambia según el estado de favorito
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}