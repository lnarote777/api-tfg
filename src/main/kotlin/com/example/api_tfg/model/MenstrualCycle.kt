package com.example.api_tfg.model

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Representa un ciclo menstrual de un usuario, incluyendo fechas, duración, flujo y fases.
 *
 * @property id Identificador único del ciclo (opcional, generado por la base de datos).
 * @property userId Identificador del usuario al que pertenece el ciclo.
 * @property startDate Fecha de inicio del ciclo (formato String, por ejemplo "yyyy-MM-dd").
 * @property endDate Fecha de finalización del ciclo (formato String).
 * @property cycleLength Duración total del ciclo en días.
 * @property bleedingDuration Duración de la menstruación en días.
 * @property averageFlow Nivel promedio de flujo menstrual durante la menstruación.
 * @property logs Lista de registros diarios (`DailyLog`) relacionados con este ciclo.
 * @property isPredicted Indica si este ciclo es una predicción o un ciclo confirmado.
 * @property phases Lista de días con sus respectivas fases del ciclo menstrual.
 */
@Document("MenstrualCycle")
data class MenstrualCycle(
    @BsonId
    var id: String? = null,
    val userId: String,
    val startDate: String,
    val endDate: String,
    val cycleLength: Int,
    val bleedingDuration: Int,
    val averageFlow: MenstrualFlowLevel,
    val logs: List<DailyLog> = listOf(),
    val isPredicted: Boolean = false,
    val phases: List<CyclePhaseDay> = listOf()
)

/**
 * Enum que representa los diferentes niveles de flujo menstrual.
 */
enum class MenstrualFlowLevel {
    LIGHT, MODERATE, HEAVY, CLOTS
}

/**
 * Enum que representa las fases del ciclo menstrual.
 */
enum class CyclePhase {
    MENSTRUATION, FOLLICULAR, OVULATION, LUTEAL
}
