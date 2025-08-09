/**
 * ExpandableMealSection.kt
 *
 * Este archivo define un componente Composable que implementa una sección expandible para
 * mostrar las comidas planificadas para un momento específico del día (desayuno, almuerzo, cena, etc.).
 * Permite al usuario expandir/contraer la sección, ver las recetas planificadas,
 * añadir nuevas recetas y gestionar las existentes.
 */
package com.noskill.anymeal.ui.components

import android.util.Log // Importar Log para depuración
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.noskill.anymeal.ui.models.PlanEntry
import com.noskill.anymeal.viewmodel.FavoritesViewModel

/**
 * Componente que muestra una sección expandible para un tipo de comida específico.
 * Incluye un encabezado clicable, una lista de recetas que se puede mostrar u ocultar,
 * y un botón para añadir más recetas.
 *
 * @param modifier Modificador opcional para personalizar el diseño
 * @param title Título de la sección (ej: "Desayuno", "Almuerzo", "Cena")
 * @param icon Icono vectorial que representa visualmente el tipo de comida
 * @param entries Lista de entradas de plan que contienen las recetas para este tipo de comida
 * @param isExpanded Estado que indica si la sección está expandida o contraída
 * @param onHeaderClick Función callback que se ejecuta cuando se hace clic en el encabezado para expandir/contraer
 * @param onAddClick Función callback que se ejecuta cuando se hace clic en el botón de añadir receta
 * @param onRecipeClick Función callback que se ejecuta cuando se hace clic en una receta específica
 * @param onDeleteEntry Función callback que se ejecuta cuando se elimina una entrada, recibe el ID del backend
 * @param favoritesViewModel ViewModel que gestiona el estado de las recetas favoritas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableMealSection(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    entries: List<PlanEntry>,
    isExpanded: Boolean,
    onHeaderClick: () -> Unit,
    onAddClick: () -> Unit,
    onRecipeClick: (Int) -> Unit,
    onDeleteEntry: (Long) -> Unit, // Acepta el ID del backend (Long)
    favoritesViewModel: FavoritesViewModel
) {
    // Obtiene los IDs de recetas favoritas del ViewModel como estado observable
    val favoriteIds by favoritesViewModel.favoriteRecipeIds.collectAsState()

    // Logging para depuración - muestra información sobre la recomposición y las entradas
    Log.d("ExpandableMealSection", "Recomposing: $title, isExpanded: $isExpanded, Entries count: ${entries.size}")
    entries.forEach { entry ->
        Log.d("ExpandableMealSection", "  Entry in list for $title: ${entry.recipe.title} (ID: ${entry.backendId})")
    }

    // Tarjeta principal que contiene toda la sección
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                // Animación de spring para que la expansión/contracción sea más natural
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy, // Menos rebote
                    stiffness = Spring.StiffnessMedium           // Rigidez media para velocidad moderada
                )
            ),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Encabezado de la sección con el título e icono
            ListItem(
                modifier = Modifier.clickable { onHeaderClick() }, // Hace que todo el encabezado sea clicable
                headlineContent = {
                    // Título principal de la sección (ej: "Desayuno")
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                supportingContent = {
                    // Texto secundario que muestra la cantidad de recetas
                    Text(text = if (entries.isEmpty()) "Sin recetas" else "${entries.size} receta(s)")
                },
                leadingContent = {
                    // Icono con fondo circular que representa el tipo de comida
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                CircleShape
                            )
                            .padding(8.dp)
                    )
                },
                trailingContent = {
                    // Icono de expansión/contracción que cambia según el estado
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Contraer" else "Expandir"
                    )
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent // Fondo transparente para el ListItem
                )
            )

            // Contenido expandible que aparece/desaparece con animación
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    // Línea divisoria entre el encabezado y el contenido
                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )

                    // Mensaje cuando no hay recetas
                    if (entries.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Añade tu primera receta a esta sección.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        // Lista de recetas cuando hay elementos
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre recetas
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            // Itera sobre cada entrada y crea un componente RecipeItem
                            entries.forEachIndexed { index, entry ->
                                // Usa una clave única para evitar problemas de recomposición
                                key("${entry.backendId}-$index") {
                                    RecipeItem(
                                        entry = entry,
                                        onClick = onRecipeClick,
                                        onDelete = { onDeleteEntry(entry.backendId) },
                                        isFavorite = entry.recipe.id in favoriteIds, // Verifica si es favorita
                                        onFavoriteClick = { favoritesViewModel.toggleFavorite(entry.recipe.id) }
                                    )
                                }
                            }
                        }
                    }

                    // Botón para añadir nueva receta
                    Button(
                        onClick = onAddClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Añadir receta")
                    }
                }
            }
        }
    }
}
