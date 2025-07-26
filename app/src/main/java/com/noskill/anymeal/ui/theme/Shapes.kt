/* --------------------------------------------------------------------
 * Archivo: Shape.kt
 * Descripción: Define las formas (shapes) globales para la aplicación,
 * asegurando una consistencia visual en la curvatura de las esquinas
 * de todos los componentes.
 * --------------------------------------------------------------------
 */
package com.noskill.anymeal.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// El objeto 'AppShapes' centraliza la definición de las formas que se
// usarán en toda la aplicación a través de MaterialTheme.
// Esto permite que componentes como Card, Button, etc., tengan un
// radio de borde consistente y predecible.

val AppShapes = Shapes(
    // Esquinas pequeñas: Usadas para componentes compactos como
    // botones, píldoras (chips), o campos de texto.
    small = RoundedCornerShape(12.dp),

    // Esquinas medianas: El estándar para la mayoría de los componentes
    // de tamaño mediano, como las tarjetas de recetas (Cards) o diálogos.
    medium = RoundedCornerShape(20.dp),

    // Esquinas grandes: Reservadas para componentes de gran tamaño que
    // necesitan un impacto visual mayor, como las hojas inferiores
    // (Bottom Sheets) o tarjetas destacadas.
    large = RoundedCornerShape(32.dp)
)