package com.example.api_tfg.dto

import com.example.api_tfg.model.MenstrualFlowLevel
import java.time.LocalDate

/**
 * Data Transfer Object para la información del ciclo menstrual.
 *
 * @property userId Identificador del usuario al que pertenece el ciclo.
 * @property startDate Fecha de inicio del ciclo en formato ISO (yyyy-MM-dd).
 * @property cycleLength Duración total del ciclo menstrual en días.
 * @property bleedingDuration Duración del periodo de sangrado en días.
 * @property averageFlow Nivel promedio del flujo menstrual durante el ciclo.
 * @property isPredicted Indica si el ciclo es una predicción (true) o un ciclo confirmado (false). Por defecto es false.
 */
data class MenstrualCycleDTO(
    val userId: String,
    val startDate: String,
    val cycleLength: Int,
    val bleedingDuration: Int,
    val averageFlow: MenstrualFlowLevel,
    val isPredicted: Boolean = false
)
