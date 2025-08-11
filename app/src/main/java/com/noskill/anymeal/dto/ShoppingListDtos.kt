/* --------------------------------------------------------------------
 * Archivo: ShoppingListDtos.kt
 * Propósito: Define los modelos de datos (DTOs) para las operaciones relacionadas
 *            con la lista de compras, incluyendo los elementos, la respuesta
 *            agrupada por categoría, la generación de la lista semanal y la
 *            petición para agregar un nuevo elemento personalizado.
 * --------------------------------------------------------------------*/

package com.noskill.anymeal.dto

import com.google.gson.annotations.SerializedName

// Modelo de datos para un elemento individual de la lista de compras.
// Incluye información como nombre, cantidad, unidad, categoría y si está marcado.
data class ShoppingItemDto(
    @SerializedName("id") val id: Long, // ID único del elemento
    @SerializedName("name") val name: String, // Nombre del producto
    @SerializedName("amount") val amount: Double?, // Cantidad del producto
    @SerializedName("unit") val unit: String?, // Unidad de medida
    @SerializedName("category") val category: String?, // Categoría del producto
    @SerializedName("isChecked") val isChecked: Boolean // Indica si el elemento está marcado como comprado
)

// Modelo de datos para la respuesta de la lista de compras agrupada por categoría.
data class ShoppingListResponse(
    @SerializedName("itemsByCategory") val itemsByCategory: Map<String, List<ShoppingItemDto>> // Mapa de categoría a lista de elementos
)

// Modelo de datos para la petición de generación de la lista de compras semanal.
data class GenerateListRequest(
    @SerializedName("startDate") val startDate: String, // Fecha de inicio del plan semanal
    @SerializedName("endDate") val endDate: String // Fecha de fin del plan semanal
)

// Modelo de datos para la petición de agregar un nuevo elemento personalizado a la lista de compras.
data class AddItemRequest(
    @SerializedName("customName") val customName: String, // Nombre personalizado del producto
    @SerializedName("amount") val amount: Double?, // Cantidad del producto
    @SerializedName("unit") val unit: String?, // Unidad de medida
    @SerializedName("category") val category: String? // Categoría del producto
)

data class EditItemRequest(
    @SerializedName("customName") val customName: String?,
    @SerializedName("amount") val amount: Double?,
    @SerializedName("unit") val unit: String?
)

data class UpdateItemRequest(
    @SerializedName("isChecked") val isChecked: Boolean
)