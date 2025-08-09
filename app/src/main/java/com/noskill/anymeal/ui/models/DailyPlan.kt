/**
 * DailyPlan.kt
 *
 * Propósito: Define el modelo de datos para representar el plan de comidas de un día específico.
 * Contiene información sobre las diferentes comidas programadas para un día, notas adicionales
 * y la fecha a la que corresponde el plan.
 */
package com.noskill.anymeal.ui.models

import java.time.LocalDate

/**
 * Modelo de datos que representa el plan de comidas para un día específico.
 * Organiza las recetas en diferentes comidas (desayuno, almuerzo, cena, etc.)
 * y permite añadir notas para la planificación diaria.
 *
 * @property meals Mapa que asocia cada tipo de comida (p.ej. "desayuno", "almuerzo") con una lista de entradas de plan
 * @property notes Notas adicionales sobre el plan del día (ingredientes a comprar, preparación, etc.)
 * @property planDate Fecha para la cual está programado este plan de comidas
 */
data class DailyPlan(
    val meals: Map<String, List<PlanEntry>> = emptyMap(),
    val notes: String = "",
    val planDate: LocalDate // Debe estar aquí
)