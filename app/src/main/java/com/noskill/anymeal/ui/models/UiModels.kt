// ========================================================================
// Archivo 1: ui/models/UiModels.kt (CORREGIDO)
// Propósito: Se unifican los modelos de UI en un solo archivo y se asegura
// que todos los IDs de recetas sean de tipo Int.
// ========================================================================
package com.noskill.anymeal.ui.models

import androidx.compose.ui.graphics.vector.ImageVector

data class RecipePreviewUi(
    val id: Int, // <-- CORREGIDO A INT
    val title: String,
    val time: String,
    val difficulty: String,
    val imageUrl: String? = null,
    val category: String
)

data class CategoryUi(
    val name: String,
    val icon: ImageVector
)

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

data class RecipeDetailUi(
    val id: Int, // <-- Ya era Int, se mantiene
    val title: String,
    val time: String,
    val difficulty: String,
    val imageUrl: String? = null,
    val description: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val category: String
)