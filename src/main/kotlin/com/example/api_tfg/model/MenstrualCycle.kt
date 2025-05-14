package com.example.api_tfg.model

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document

@Document("MenstrualCycle")
data class MenstrualCycle(
    @BsonId
    val id: String? = null,
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

enum class MenstrualFlowLevel {
    LIGHT, MODERATE, HEAVY, CLOTS
}

enum class CyclePhase {
    MENSTRUATION, FOLLICULAR, OVULATION, LUTEAL
}
