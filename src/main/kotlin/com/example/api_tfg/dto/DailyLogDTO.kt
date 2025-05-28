package com.example.api_tfg.dto

import com.example.api_tfg.model.MenstrualFlowLevel
import java.time.LocalDate

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