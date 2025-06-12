package com.example.api_tfg.model

/**
 * Representa un día específico dentro del ciclo menstrual, junto con la fase correspondiente.
 *
 * @property date Fecha del día en formato String (por ejemplo, "yyyy-MM-dd").
 * @property phase Fase del ciclo menstrual en ese día (menstruación, ovulación, etc.).
 */
data class CyclePhaseDay(
    val date: String,
    val phase: CyclePhase
)