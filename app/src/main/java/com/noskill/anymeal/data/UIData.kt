/* --------------------------------------------------------------------
 * Archivo: UIData.kt (NUEVO)
 * Ubicación: com/noskill/anymeal/data/UIData.kt
 * Descripción: Archivo dedicado para la configuración de la UI,
 * incluyendo la barra de navegación y las categorías.
 * --------------------------------------------------------------------
 */
package com.noskill.anymeal.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.ShoppingCart
import com.noskill.anymeal.navigation.Screen
import com.noskill.anymeal.ui.models.CategoryUi
import com.noskill.anymeal.ui.models.NavItem

// Lista para la barra de navegación con iconos actualizados
val navItems = listOf(
    NavItem(
        label = "Inicio",
        icon = Icons.Outlined.Home,
        route = Screen.Home.route
    ),
    NavItem(
        label = "Plan",
        icon = Icons.Outlined.ListAlt,
        route = Screen.Plan.route
    ),
    NavItem(
        label = "Lista",
        icon = Icons.Outlined.ShoppingCart,
        route = Screen.ShoppingList.route
    ),
    NavItem(
        label = "Perfil",
        icon = Icons.Outlined.AccountCircle,
        route = Screen.Profile.route
    ),
)

// Lista para las categorías
val categories = listOf(
    CategoryUi("Desayuno", Icons.Filled.FreeBreakfast),
    CategoryUi("Almuerzo", Icons.Filled.LunchDining),
    CategoryUi("Cena", Icons.Filled.DinnerDining),
    CategoryUi("Snacks", Icons.Filled.Fastfood)
)