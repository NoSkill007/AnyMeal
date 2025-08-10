/**
 * Result.kt
 * 
 * Propósito: Define una clase sellada genérica que representa los diferentes estados 
 * de una operación asíncrona, especialmente para peticiones de red. Permite manejar 
 * de forma segura y tipada los casos de éxito, error y carga.
 */
package com.noskill.anymeal.util

/**
 * Clase sellada genérica que representa los posibles resultados de una operación.
 * Implementa el patrón Result para manejar estados de operaciones asíncronas.
 * 
 * @param T Tipo de datos que se espera en caso de éxito
 */
sealed class Result<out T> {
    /**
     * Representa una operación exitosa con datos disponibles.
     * 
     * @param data Los datos obtenidos tras la operación exitosa
     */
    data class Success<out T>(val data: T) : Result<T>()
    
    /**
     * Representa un error en la operación.
     * 
     * @param message Mensaje descriptivo del error ocurrido
     */
    data class Error(val message: String) : Result<Nothing>()
    
    /**
     * Representa que una operación está en proceso y aún no ha finalizado.
     * Object singleton ya que no requiere datos adicionales.
     */
    object Loading : Result<Nothing>()
}
