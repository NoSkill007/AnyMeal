/**
 * NutritionSummaryCard.kt
 *
 * Este archivo define un componente Composable que muestra un resumen visual de la información
 * nutricional de una comida o receta. Presenta los valores clave (calorías, proteínas, carbohidratos
 * y grasas) en un formato de tarjeta con elementos distribuidos de manera uniforme.
 */
package com.noskill.anymeal.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noskill.anymeal.ui.models.NutritionInfo

/**
 * Componente que muestra un resumen de información nutricional en formato de tarjeta.
 * Presenta los valores de calorías, proteínas, carbohidratos y grasas en una fila
 * con diseño uniforme y claro.
 *
 * @param nutritionInfo Objeto que contiene la información nutricional a mostrar
 * @param modifier Modificador opcional para personalizar el diseño
 */
@Composable
fun NutritionSummaryCard(
    nutritionInfo: NutritionInfo,
    modifier: Modifier = Modifier
) {
    // Tarjeta principal con fondo semitransparente y bordes redondeados
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp), // Bordes bastante redondeados para estética moderna
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) // Color de fondo suave y semitransparente
        )
    ) {
        // Fila que contiene los diferentes elementos nutricionales
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp), // Espaciado vertical para mejor legibilidad
            horizontalArrangement = Arrangement.SpaceAround, // Distribuye los elementos uniformemente
            verticalAlignment = Alignment.CenterVertically // Alineación vertical centrada
        ) {
            // Componentes individuales para cada valor nutricional
            NutritionItem(value = "${nutritionInfo.calories}", label = "Kcal") // Calorías
            NutritionItem(value = "${nutritionInfo.protein}g", label = "Proteína") // Proteínas en gramos
            NutritionItem(value = "${nutritionInfo.carbs}g", label = "Carbs") // Carbohidratos en gramos
            NutritionItem(value = "${nutritionInfo.fat}g", label = "Grasa") // Grasas en gramos
        }
    }
}

/**
 * Componente privado que muestra un elemento individual de información nutricional.
 * Consta de un valor numérico destacado y una etiqueta descriptiva debajo.
 *
 * @param value Valor nutricional a mostrar (ya formateado como string con unidades si corresponde)
 * @param label Etiqueta descriptiva del valor (ej: "Proteína", "Kcal")
 * @param modifier Modificador opcional para personalizar el diseño
 */
@Composable
private fun NutritionItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    // Columna para organizar el valor y la etiqueta verticalmente
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally // Centra horizontalmente el contenido
    ) {
        // Texto del valor nutricional, destacado con color primario y negrita
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary // Usa el color primario para destacar
        )
        // Texto de la etiqueta, más pequeño y con color menos prominente
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), // Tamaño reducido para jerarquía visual
            color = MaterialTheme.colorScheme.onSurfaceVariant // Color secundario para menor énfasis
        )
    }
}
