/**
 * ShoppingItem.kt
 *
 * Propósito: Define el modelo de datos para representar un elemento en la lista de compras
 * del usuario. Cada item contiene información sobre el producto, cantidad, categoría y
 * estado de verificación, permitiendo organizar y gestionar eficientemente las compras.
 */
package com.noskill.anymeal.ui.models

/**
 * Modelo de datos que representa un elemento individual en la lista de compras.
 * Los elementos de compra pueden generarse automáticamente a partir de recetas
 * o ser añadidos manualmente por el usuario.
 *
 * @property id Identificador único del elemento en la lista de compras
 * @property name Nombre del producto o ingrediente a comprar
 * @property quantity Cantidad y unidad de medida del producto (ej. "500g", "2 unidades")
 * @property category Categoría a la que pertenece el producto para facilitar la organización (ej. "Lácteos", "Carnes")
 * @property isChecked Estado que indica si el usuario ha marcado el elemento como comprado
 */
data class ShoppingItem(
    val id: Long,
    val name: String,
    val quantity: String,
    val category: String,
    var isChecked: Boolean = false
)