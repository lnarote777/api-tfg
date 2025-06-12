package com.example.api_tfg.dto

import com.example.api_tfg.model.MenstrualFlowLevel

/**
 * Data Transfer Object para registrar información diaria relacionada con el ciclo menstrual.
 *
 * @property date Fecha del registro en formato String (ejemplo: "YYYY-MM-DD").
 * @property hasMenstruation Indica si hay menstruación en la fecha indicada.
 * @property menstrualFlow Nivel del flujo menstrual (puede ser nulo si no hay menstruación).
 * @property sexualActivity Lista opcional de actividades sexuales registradas.
 * @property mood Lista opcional de estados de ánimo registrados.
 * @property symptoms Lista opcional de síntomas registrados.
 * @property vaginalDischarge Lista opcional de tipos de flujo vaginal registrados.
 * @property physicalActivity Lista opcional de actividades físicas realizadas.
 * @property pillsTaken Lista opcional de pastillas o medicación tomada.
 * @property waterIntake Cantidad de agua consumida en litros (opcional).
 * @property weight Peso corporal en kilogramos (opcional).
 * @property notes Notas adicionales o comentarios (opcional).
 */
data class DailyLogDTO(
    val date: String,
    val hasMenstruation: Boolean = false,
    val menstrualFlow: MenstrualFlowLevel? = null,
    val sexualActivity: MutableList<String?> = mutableListOf(),
    val mood:  MutableList<String?> = mutableListOf(),
    val symptoms: MutableList<String?> = mutableListOf(),
    val vaginalDischarge: MutableList<String?> = mutableListOf(),
    val physicalActivity: MutableList<String?> = mutableListOf(),
    val pillsTaken: MutableList<String?> = mutableListOf(),
    val waterIntake: Double? = null,
    val weight: Double? = null,
    val notes: String? = null
)