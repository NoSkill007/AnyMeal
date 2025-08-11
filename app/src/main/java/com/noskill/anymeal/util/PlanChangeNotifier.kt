/**
 * PlanChangeNotifier.kt
 *
 * Propósito: Notificador global para cambios en el plan de comidas.
 * Permite que diferentes ViewModels se comuniquen cuando hay modificaciones
 * en el plan que requieren actualizar la lista de compras con la fecha correcta.
 */
package com.noskill.anymeal.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.time.LocalDate

/**
 * Evento que se emite cuando cambia el plan.
 * Incluye la fecha que fue modificada para sincronización.
 */
data class PlanChangeEvent(
    val modifiedDate: LocalDate? = null
)

/**
 * Objeto singleton que gestiona las notificaciones de cambios en el plan.
 * Utiliza SharedFlow para permitir múltiples observadores y sincronización de fechas.
 */
object PlanChangeNotifier {

    // SharedFlow privado para emitir eventos de cambio con información de fecha
    private val _planChanged = MutableSharedFlow<PlanChangeEvent>(replay = 0)

    // SharedFlow público para que otros componentes puedan observar
    val planChanged: SharedFlow<PlanChangeEvent> = _planChanged.asSharedFlow()

    /**
     * Notifica que el plan ha cambiado en una fecha específica.
     * Esto permite que ShoppingListScreen se sincronice con la semana correcta.
     */
    suspend fun notifyPlanChanged(modifiedDate: LocalDate? = null) {
        _planChanged.emit(PlanChangeEvent(modifiedDate))
    }
}
