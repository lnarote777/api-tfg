package com.example.api_tfg.dto

import com.example.api_tfg.model.MenstrualFlowLevel
import java.time.LocalDate

data class DailyLogDTO(
    val date: String,
    val hasMenstruation: Boolean = false,
    val menstrualFlow: MenstrualFlowLevel? = null,
    val sexualActivity: List<String> = emptyList(),
    val mood: List<String> = emptyList(),
    val symptoms: List<String> = emptyList(),
    val vaginalDischarge: List<String> = emptyList(),
    val physicalActivity: List<String> = emptyList(),
    val pillsTaken: List<String> = emptyList(),
    val waterIntake: Double? = null,
    val weight: Double? = null,
    val notes: String? = null
)