/**
 * AchievementsScreen.kt
 *
 * Propósito: Define la pantalla de logros (achievements) de la aplicación AnyMeal.
 * Muestra los diferentes hitos que el usuario puede desbloquear, ofreciendo una
 * experiencia gamificada que incentiva el uso continuo de las funcionalidades de la app.
 * Implementa una TopAppBar para mantener una navegación consistente con otras pantallas.
 */
@file:OptIn(ExperimentalMaterial3Api::class)
package com.noskill.anymeal.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.noskill.anymeal.ui.models.Achievement
import com.noskill.anymeal.viewmodel.AchievementsViewModel
import com.noskill.anymeal.viewmodel.AchievementsViewModelFactory
import com.noskill.anymeal.viewmodel.FavoritesViewModel
import com.noskill.anymeal.viewmodel.PlannerViewModel

/**
 * Composable principal que define la pantalla de logros del usuario.
 * Muestra una lista de todos los logros disponibles, destacando los que ya han sido desbloqueados
 * y proporcionando un resumen del progreso del usuario.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas
 * @param plannerViewModel ViewModel que proporciona datos sobre planes de comidas para calcular logros
 * @param favoritesViewModel ViewModel que proporciona datos sobre recetas favoritas para calcular logros
 */
@Composable
fun AchievementsScreen(
    navController: NavController,
    plannerViewModel: PlannerViewModel,
    favoritesViewModel: FavoritesViewModel
) {
    // Creación del ViewModel específico para esta pantalla con sus dependencias
    val factory = AchievementsViewModelFactory(plannerViewModel, favoritesViewModel)
    val achievementsViewModel: AchievementsViewModel = viewModel(factory = factory)

    // Recolección del estado de los logros y cálculo de estadísticas
    val achievements by achievementsViewModel.achievements.collectAsState()
    val unlockedCount = achievements.count { it.isUnlocked }

    // Estructura principal de la pantalla con barra superior de navegación
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tus Hitos", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        // Lista de logros con resumen inicial
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Resumen del progreso del usuario
                Column {
                    Text(
                        "¡Sigue así!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Has desbloqueado $unlockedCount de ${achievements.size} logros.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            // Lista de elementos de logro
            items(achievements) { achievement ->
                AchievementItem(achievement = achievement)
            }
        }
    }
}

/**
 * Composable que representa un elemento individual de logro en la lista.
 * Aplica efectos visuales para diferenciar los logros desbloqueados de los bloqueados,
 * como la opacidad y el color del icono.
 *
 * @param achievement Modelo de datos del logro a mostrar
 */
@Composable
private fun AchievementItem(achievement: Achievement) {
    // Animaciones para los cambios de estado visual basados en si el logro está desbloqueado
    val alpha by animateFloatAsState(targetValue = if (achievement.isUnlocked) 1f else 0.5f, label = "alphaAnim")
    val iconColor by animateColorAsState(targetValue = if (achievement.isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, label = "iconColorAnim")

    // Tarjeta que contiene la información del logro
    Card(
        modifier = Modifier.fillMaxWidth().alpha(alpha),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icono representativo del logro
            Icon(
                imageVector = achievement.icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
            // Información textual del logro
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
