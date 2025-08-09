/**
 * Shapes.kt
 *
 * Propósito: Define las formas (shapes) utilizadas en toda la aplicación AnyMeal para
 * asegurar consistencia visual en la curvatura de las esquinas de los diferentes componentes
 * de la interfaz. Estos valores son utilizados por el sistema de temas de Jetpack Compose.
 */
package com.noskill.anymeal.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Definición de las formas (shapes) que se utilizarán en toda la aplicación.
 * Proporciona una colección de formas con diferentes radios de borde para mantener
 * la consistencia visual en los componentes de la interfaz.
 *
 * Estas formas se utilizan a través de MaterialTheme en toda la aplicación, permitiendo
 * que componentes como Card, Button, TextField, etc. tengan radios de borde consistentes.
 */
val AppShapes = Shapes(
    /**
     * Esquinas con radio pequeño (12dp)
     * Usadas para elementos compactos como botones, chips (píldoras) o campos de texto.
     */
    small = RoundedCornerShape(12.dp),

    /**
     * Esquinas con radio mediano (20dp)
     * Estándar para la mayoría de componentes como tarjetas de recetas (Cards) o diálogos.
     * Este tamaño es el más utilizado en la aplicación.
     */
    medium = RoundedCornerShape(20.dp),

    /**
     * Esquinas con radio grande (32dp)
     * Utilizadas para componentes de gran tamaño que requieren mayor impacto visual,
     * como bottom sheets o tarjetas destacadas.
     */
    large = RoundedCornerShape(32.dp)
)
