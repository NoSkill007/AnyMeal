/**
 * NutritionInfo.kt
 *
 * Propósito: Define el modelo de datos para representar la información nutricional
 * de las recetas en la aplicación. Almacena valores básicos de macronutrientes y calorías
 * que son importantes para los usuarios que quieren seguir sus objetivos nutricionales.
 */
package com.noskill.anymeal.ui.models

/**
 * Modelo de datos que representa la información nutricional de una receta.
 * Contiene los valores nutricionales básicos que son relevantes para la mayoría de usuarios
 * al planificar sus comidas según objetivos dietéticos.
 *
 * @property calories Cantidad de calorías totales en la receta, expresada en kcal
 * @property protein Cantidad de proteínas en la receta, expresada en gramos
 * @property carbs Cantidad de carbohidratos en la receta, expresada en gramos
 * @property fat Cantidad de grasas en la receta, expresada en gramos
 */
data class NutritionInfo(
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0
)
