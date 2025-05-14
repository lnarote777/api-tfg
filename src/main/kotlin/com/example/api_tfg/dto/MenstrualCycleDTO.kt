package com.example.api_tfg.dto

import com.example.api_tfg.model.MenstrualFlowLevel
import java.time.LocalDate

data class MenstrualCycleDTO(
    val userId: String,
    val startDate: String,
    val cycleLength: Int,
    val bleedingDuration: Int,
    val averageFlow: MenstrualFlowLevel,
    val isPredicted: Boolean = false
)
