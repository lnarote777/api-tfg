package com.example.api_tfg.model

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document("DailyLog")
data class DailyLog(
    @BsonId
    val id: String? = null,
    val userId: String,
    val date: String,
    val hasMenstruation: Boolean = false,
    val menstrualFlow: MenstrualFlowLevel? = null,
    val sexualActivity:  MutableList<String?> = mutableListOf(),
    val mood:  MutableList<String?> = mutableListOf(),
    val symptoms:  MutableList<String?> = mutableListOf(),
    val vaginalDischarge: MutableList<String?> = mutableListOf(),
    val physicalActivity: MutableList<String?> = mutableListOf(),
    val pillsTaken:  MutableList<String?> = mutableListOf(),
    val waterIntake: Double? = null,
    val weight: Double? = null,
    val notes: String? = null
)