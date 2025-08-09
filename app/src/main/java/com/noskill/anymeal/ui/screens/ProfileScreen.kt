/**
 * ProfileScreen.kt
 *
 * Propósito: Define la pantalla de perfil de usuario de la aplicación AnyMeal.
 * Muestra la información personal del usuario, resumen de logros y opciones de configuración.
 * Permite al usuario editar su perfil, gestionar preferencias como el tema oscuro,
 * acceder a favoritos, soporte y cerrar sesión en la aplicación.
 */
package com.noskill.anymeal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.noskill.anymeal.data.navItems
import com.noskill.anymeal.navigation.Screen
import com.noskill.anymeal.ui.components.FloatingBottomNavBar
import com.noskill.anymeal.util.Result
import com.noskill.anymeal.viewmodel.FavoritesViewModel
import com.noskill.anymeal.viewmodel.PlannerViewModel
import com.noskill.anymeal.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.map

/**
 * Composable principal que define la pantalla de perfil de usuario.
 * Gestiona la visualización de información personal, resumen de actividad,
 * y opciones de configuración, integrando datos de varios ViewModels.
 *
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas
 * @param isDarkTheme Indica si el tema oscuro está actualmente activado
 * @param onThemeChange Callback que se invoca cuando el usuario cambia la preferencia de tema
 * @param onLogoutConfirmed Callback que se invoca cuando el usuario confirma el cierre de sesión
 * @param plannerViewModel ViewModel que proporciona datos sobre los planes del usuario
 * @param favoritesViewModel ViewModel que proporciona datos sobre las recetas favoritas
 * @param profileViewModel ViewModel que gestiona la información del perfil de usuario
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLogoutConfirmed: () -> Unit,
    plannerViewModel: PlannerViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    // Estado para controlar la visibilidad del diálogo de confirmación de cierre de sesión
    val openDialog = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Estado para la navegación inferior
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val selectedIndex = navItems.indexOfFirst { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    }.coerceAtLeast(0)

    // Se obtienen los estados del ProfileViewModel
    val profileUiState by profileViewModel.uiState.collectAsState()
    val user = profileUiState.user

    // Se obtienen los estados del PlannerViewModel
    val planResult by plannerViewModel.planState.collectAsState()

    // Efecto para cargar el perfil cada vez que la pantalla se vuelve activa
    // Esto asegura que si EditProfileScreen actualizó datos, ProfileScreen los obtenga
    LaunchedEffect(Unit) {
        profileViewModel.loadUserProfile()
    }

    // Se extrae el nombre y el correo del estado del usuario
    val userName = user?.username ?: "Cargando..."
    val userEmail = user?.email ?: ""

    // Se calculan las estadísticas del plan solo si los datos se han cargado correctamente
    val (totalPlannedDays, totalRecipesAdded) = when (val state = planResult) {
        is Result.Success -> {
            val planData = state.data
            val days = planData.size
            val recipes = planData.values.sumOf { dailyPlan ->
                dailyPlan.meals.values.sumOf { it.size }
            }
            Pair(days, recipes)
        }
        else -> Pair(0, 0) // Valores por defecto para los estados de carga o error
    }

    // Obtiene el total de recetas favoritas
    val totalFavorites by favoritesViewModel.favoriteRecipeIds.map { it.size }.collectAsState(initial = 0)

    // Estructura principal de la pantalla con la barra de navegación inferior flotante
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
        ) { innerPadding ->
            // Columna principal con scroll vertical
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(innerPadding)
                    .padding(bottom = 80.dp) // Espacio para la barra de navegación
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Cabecera con información del usuario y botón de edición
                    ProfileHeader(
                        name = userName,
                        email = userEmail,
                        onEditClick = {
                            navController.navigate(Screen.EditProfile.route)
                        }
                    )

                    // Tarjeta de resumen de logros y progreso
                    AchievementsSummaryCard(
                        navController = navController,
                        totalPlannedDays = totalPlannedDays,
                        totalRecipesAdded = totalRecipesAdded,
                        totalFavorites = totalFavorites
                    )

                    // Sección de menú con opciones de configuración
                    MenuSection(
                        navController = navController,
                        isDarkTheme = isDarkTheme,
                        onThemeChange = onThemeChange
                    )

                    // Botón de cierre de sesión
                    LogoutButton(onClick = { openDialog.value = true })
                }
            }
        }

        // Barra de navegación inferior flotante
        FloatingBottomNavBar(
            items = navItems,
            selectedIndex = selectedIndex,
            onItemSelected = { index ->
                val destination = navItems[index].route
                if (currentDestination?.route != destination) {
                    navController.navigate(destination) {
                        // Configura la navegación para mantener un comportamiento correcto
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // Diálogo de confirmación para cerrar sesión
    if (openDialog.value) {
        LogoutConfirmationDialog(
            onDismiss = { openDialog.value = false },
            onConfirm = {
                openDialog.value = false
                onLogoutConfirmed()
            }
        )
    }
}

/**
 * Composable que muestra la cabecera con información del perfil de usuario.
 * Incluye avatar, nombre, correo electrónico y botón para editar el perfil.
 *
 * @param name Nombre del usuario a mostrar
 * @param email Correo electrónico del usuario
 * @param onEditClick Callback que se invoca cuando se presiona el botón de editar
 */
@Composable
private fun ProfileHeader(name: String, email: String, onEditClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceContainerHigh,
                            MaterialTheme.colorScheme.surfaceContainer
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar de usuario (icono de persona)
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Foto de Perfil",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        .padding(12.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                // Información textual del usuario
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                // Botón para editar perfil
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar Perfil", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

/**
 * Composable que muestra una tarjeta con resumen de logros y progreso del usuario.
 * Permite navegar a la pantalla detallada de logros al hacer clic.
 *
 * @param navController Controlador de navegación para ir a la pantalla de logros
 * @param totalPlannedDays Número total de días que el usuario ha planificado
 * @param totalRecipesAdded Número total de recetas que el usuario ha añadido a planes
 * @param totalFavorites Número total de recetas marcadas como favoritas
 */
@Composable
private fun AchievementsSummaryCard(
    navController: NavController,
    totalPlannedDays: Int,
    totalRecipesAdded: Int,
    totalFavorites: Int
) {
    Column {
        Text("Tus Hitos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
        Card(
            onClick = { navController.navigate(Screen.Achievements.route) },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Cabecera de la tarjeta de logros
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Resumen de Progreso", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Toca para ver todos tus logros", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                // Estadísticas de progreso del usuario
                Text("• $totalPlannedDays Días Planificados", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                Text("• $totalRecipesAdded Recetas Añadidas", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                Text("• $totalFavorites Recetas Favoritas", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

/**
 * Composable que muestra la sección de menú con opciones de configuración.
 * Incluye enlaces a favoritos, toggle de tema oscuro, ayuda e información.
 *
 * @param navController Controlador de navegación para ir a otras pantallas
 * @param isDarkTheme Indica si el tema oscuro está actualmente activado
 * @param onThemeChange Callback que se invoca cuando el usuario cambia la preferencia de tema
 */
@Composable
private fun MenuSection(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column {
            // Enlace a recetas favoritas
            MenuItem(
                title = "Mis Recetas Favoritas",
                icon = Icons.Outlined.FavoriteBorder,
                onClick = { navController.navigate(Screen.Favorites.route) }
            )
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            // Toggle para cambiar entre tema claro y oscuro
            MenuItem(
                title = "Tema Oscuro",
                icon = Icons.Outlined.DarkMode
            ) {
                Switch(checked = isDarkTheme, onCheckedChange = onThemeChange)
            }
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            // Enlace a preguntas frecuentes y soporte
            MenuItem(
                title = "Ayuda y Soporte",
                icon = Icons.Outlined.HelpOutline,
                onClick = { navController.navigate(Screen.Faq.route) }
            )
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            // Enlace a información de la aplicación
            MenuItem(
                title = "Información de la App",
                icon = Icons.Outlined.Info,
                onClick = { navController.navigate(Screen.AppVersion.route) }
            )
        }
    }
}

/**
 * Composable que representa un elemento individual del menú de opciones.
 * Muestra un icono, título y contenido personalizable en la parte derecha.
 *
 * @param title Título del elemento de menú
 * @param icon Icono que representa visualmente la opción
 * @param onClick Callback opcional que se invoca al hacer clic en el elemento
 * @param trailingContent Contenido personalizado a mostrar en la parte derecha (opcional)
 */
@Composable
private fun MenuItem(
    title: String,
    icon: ImageVector,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        if (trailingContent != null) {
            trailingContent()
        } else if (onClick != null) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/**
 * Composable que muestra el botón de cierre de sesión.
 * Utiliza colores de error para destacar su importancia y carácter destructivo.
 *
 * @param onClick Callback que se invoca cuando se presiona el botón
 */
@Composable
private fun LogoutButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TextButton(onClick = onClick) {
            Icon(Icons.Default.Logout, contentDescription = "Cerrar Sesión", tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.width(8.dp))
            Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LogoutConfirmationDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Sí, salir", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("¿Cerrar sesión?") },
        text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
        shape = RoundedCornerShape(20.dp),
    )
}