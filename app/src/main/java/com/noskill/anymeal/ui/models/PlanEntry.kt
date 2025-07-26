package com.noskill.anymeal.ui.models

data class PlanEntry(
        val id: Long, // Debe ser Long
        val backendId: Long,
        val recipe: RecipePreviewUi
)