// --- PASO 3: Modelo de UI (Capa de Presentación) ---
// Archivo: ui/models/ShoppingItem.kt
package com.noskill.anymeal.ui.models

data class ShoppingItem(
    val id: Long,
    val name: String,
    val quantity: String,
    val category: String,
    var isChecked: Boolean = false
)