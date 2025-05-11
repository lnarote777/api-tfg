package com.example.api_tfg.dto

import com.example.api_tfg.model.MenstrualFlowLevel
import java.time.LocalDate

data class MenstrualCycleDTO(
    val userId: String,
    val startDate: LocalDate,
    val cycleLength: Int,
    val bleedingDuration: Int,
    val averageFlow: MenstrualFlowLevel,
    val symptoms: List<String> = listOf(),
    val moodChanges: List<String> = listOf(),
    val notes: String? = null,
    val isPredicted: Boolean = false
)
