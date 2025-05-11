package com.example.api_tfg.model

import java.time.LocalDate

data class CyclePhaseDay(
    val date: LocalDate,
    val phase: CyclePhase
)