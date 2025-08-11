/*
 * PlanChangeNotifier.kt
 *
 * Sistema de notificaciones para comunicación en tiempo real entre pantallas
 * Permite que los cambios en el plan se reflejen automáticamente en la shopping list
 */

package com.noskill.anymeal.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.time.LocalDate

/**
 * Evento que se dispara cuando hay cambios en el plan
 */
data class PlanChangeEvent(
    val modifiedDate: LocalDate? = null,
    val action: PlanAction = PlanAction.UNKNOWN,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Tipos de acciones que pueden ocurrir en el plan
 */
enum class PlanAction {
    RECIPE_ADDED,    // Se agregó una receta al plan
    RECIPE_REMOVED,  // Se eliminó una receta del plan
    RECIPE_EDITED,   // Se editó una receta en el plan
    PLAN_REGENERATED, // Se regeneró todo el plan
    UNKNOWN          // Acción no especificada
}

/**
 * Notificador global para cambios en el plan
 * Implementa patrón Observer para comunicación entre ViewModels y Screens
 */
object PlanChangeNotifier {

    // SharedFlow para emisión de eventos de cambio
    private val _planChanged = MutableSharedFlow<PlanChangeEvent>(
        replay = 0,  // No guardar eventos pasados
        extraBufferCapacity = 10 // Buffer para eventos concurrentes
    )

    // Exposición pública como SharedFlow inmutable
    val planChanged: SharedFlow<PlanChangeEvent> = _planChanged.asSharedFlow()

    /**
     * Notifica que se agregó una receta al plan en una fecha específica
     */
    suspend fun notifyRecipeAdded(date: LocalDate) {
        val event = PlanChangeEvent(
            modifiedDate = date,
            action = PlanAction.RECIPE_ADDED
        )
        _planChanged.emit(event)
        println("🔔 PlanChangeNotifier: Recipe added on $date")
    }

    /**
     * Notifica que se eliminó una receta del plan en una fecha específica
     */
    suspend fun notifyRecipeRemoved(date: LocalDate) {
        val event = PlanChangeEvent(
            modifiedDate = date,
            action = PlanAction.RECIPE_REMOVED
        )
        _planChanged.emit(event)
        println("🔔 PlanChangeNotifier: Recipe removed on $date")
    }

    /**
     * Notifica que se editó una receta del plan en una fecha específica
     */
    suspend fun notifyRecipeEdited(date: LocalDate) {
        val event = PlanChangeEvent(
            modifiedDate = date,
            action = PlanAction.RECIPE_EDITED
        )
        _planChanged.emit(event)
        println("🔔 PlanChangeNotifier: Recipe edited on $date")
    }

    /**
     * Notifica cambios generales en el plan (compatibilidad con código existente)
     */
    suspend fun notifyPlanChanged(date: LocalDate? = null) {
        val event = PlanChangeEvent(
            modifiedDate = date,
            action = if (date != null) PlanAction.RECIPE_ADDED else PlanAction.PLAN_REGENERATED
        )
        _planChanged.emit(event)
        println("🔔 PlanChangeNotifier: Plan changed ${if (date != null) "on $date" else "globally"}")
    }

    /**
     * Notifica que se regeneró completamente el plan
     */
    suspend fun notifyPlanRegenerated() {
        val event = PlanChangeEvent(
            modifiedDate = null,
            action = PlanAction.PLAN_REGENERATED
        )
        _planChanged.emit(event)
        println("🔔 PlanChangeNotifier: Plan completely regenerated")
    }
}
