/**
 * Achievement.kt
 *
 * Propósito: Define el modelo de datos para representar los logros (achievements)
 * que los usuarios pueden desbloquear en la aplicación. Cada logro contiene información
 * sobre su título, descripción, icono y estado de desbloqueo.
 */
package com.noskill.anymeal.ui.models

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Modelo de datos que representa un logro (achievement) en la aplicación.
 * Los logros son recompensas que los usuarios pueden desbloquear
 * al realizar ciertas acciones o alcanzar objetivos dentro de la aplicación.
 *
 * @property title Título corto que identifica el logro
 * @property description Texto explicativo que describe cómo desbloquear el logro
 * @property icon Vector gráfico que representa visualmente el logro
 * @property isUnlocked Estado que indica si el usuario ha desbloqueado este logro
 */
data class Achievement(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isUnlocked: Boolean
)