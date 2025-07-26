package com.noskill.anymeal.ui.models

import androidx.compose.ui.graphics.vector.ImageVector

data class Achievement(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isUnlocked: Boolean
)