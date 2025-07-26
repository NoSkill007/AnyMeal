// --- PASO 1: DTOs de Red (Data Transfer Objects) ---
// Archivo: dto/ShoppingListDtos.kt
// (Paquete corregido para coincidir con tu estructura)
package com.noskill.anymeal.dto

import com.google.gson.annotations.SerializedName

data class ShoppingItemDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("amount") val amount: Double?,
    @SerializedName("unit") val unit: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("isChecked") val isChecked: Boolean
)

data class ShoppingListResponse(
    @SerializedName("itemsByCategory") val itemsByCategory: Map<String, List<ShoppingItemDto>>
)

data class GenerateListRequest(
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String
)

data class AddItemRequest(
    @SerializedName("customName") val customName: String,
    @SerializedName("amount") val amount: Double?,
    @SerializedName("unit") val unit: String?,
    @SerializedName("category") val category: String?
)

data class UpdateItemRequest(
    @SerializedName("isChecked") val isChecked: Boolean
)