/**
 * DateUtils.kt
 *
 * Propósito: Utilidades centralizadas para el manejo consistente de fechas
 * en toda la aplicación. Asegura que todas las pantallas usen la misma
 * lógica para calcular semanas, respetando las fechas seleccionadas exactas.
 */
package com.noskill.anymeal.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*

object DateUtils {

    /**
     * Obtiene el lunes de la semana que contiene la fecha dada.
     * PERO respeta la fecha original si está dentro de la misma semana.
     */
    fun getStartOfWeek(date: LocalDate): LocalDate {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }

    /**
     * Obtiene el domingo de la semana que contiene la fecha dada.
     */
    fun getEndOfWeek(date: LocalDate): LocalDate {
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
    }

    /**
     * Calcula las fechas de inicio y fin de semana que INCLUYE la fecha dada.
     * Esta función respeta cualquier día de la semana como válido.
     */
    fun getWeekDateStringsForDate(targetDate: LocalDate): Pair<String, String> {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val weekStart = getStartOfWeek(targetDate)
        val weekEnd = getEndOfWeek(targetDate)

        return Pair(
            weekStart.format(formatter),
            weekEnd.format(formatter)
        )
    }

    /**
     * Calcula las fechas de inicio y fin de semana para un offset dado
     * desde la fecha actual.
     */
    fun getWeekDateStrings(weekOffset: Int): Pair<String, String> {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val startOfCurrentWeek = getStartOfWeek(LocalDate.now())
        val targetWeekStart = startOfCurrentWeek.plusWeeks(weekOffset.toLong())
        val targetWeekEnd = getEndOfWeek(targetWeekStart)

        return Pair(
            targetWeekStart.format(formatter),
            targetWeekEnd.format(formatter)
        )
    }

    /**
     * Calcula qué offset de semana corresponde a una fecha específica
     * desde la semana actual. CORREGIDO completamente.
     */
    fun getWeekOffsetForDate(targetDate: LocalDate): Int {
        val currentWeekStart = getStartOfWeek(LocalDate.now())
        val targetWeekStart = getStartOfWeek(targetDate)

        // CORREGIDO: Usar ChronoUnit.WEEKS para calcular diferencia exacta
        return java.time.temporal.ChronoUnit.WEEKS.between(currentWeekStart, targetWeekStart).toInt()
    }

    /**
     * NUEVA FUNCIÓN: Verifica si una fecha específica está en la semana
     * representada por un weekOffset dado.
     */
    fun isDateInWeekOffset(date: LocalDate, weekOffset: Int): Boolean {
        val (weekStartStr, weekEndStr) = getWeekDateStrings(weekOffset)
        val weekStart = LocalDate.parse(weekStartStr)
        val weekEnd = LocalDate.parse(weekEndStr)

        return !date.isBefore(weekStart) && !date.isAfter(weekEnd)
    }
}
