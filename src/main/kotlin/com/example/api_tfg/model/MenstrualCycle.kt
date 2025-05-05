package com.example.api_tfg.model

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document("MenstrualCycle")
data class MenstrualCycle(
    @BsonId
    val id: String? = null,
    val userId: String,
    val startDate: LocalDate,
    val cycleLength: Int,
    val bleedingDuration: Int,
    val averageFlow: MenstrualFlowLevel,
    val symptoms: List<String> = listOf(),
    val moodChanges: List<String> = listOf(),
    val registeredAt: LocalDate = LocalDate.now(),
    val notes: String? = null
)

enum class MenstrualFlowLevel {
    LIGHT, MODERATE, HEAVY, CLOTS
}
