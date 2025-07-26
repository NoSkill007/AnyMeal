package com.noskill.anymeal.ui.models
import java.time.LocalDate

data class DailyPlan(
    val meals: Map<String, List<PlanEntry>> = emptyMap(),
    val notes: String = "",
    val planDate: LocalDate // Debe estar aquí
)