package com.example.api_tfg.model

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document("DailyLog")
data class DailyLog(
    @BsonId
    val id: String? = null,                       // MongoDB ID
    val userId: String,                           // Reference to the user
    val date: String,                          // Log date
    val hasMenstruation: Boolean = false,
    val menstrualFlow: MenstrualFlowLevel? = null,
    val sexualActivity: List<String> = emptyList(),  // e.g., "Intercourse", "Desire", "Masturbation"
    val mood: List<String> = emptyList(),            // e.g., "Happy", "Irritable", "Sad"
    val symptoms: List<String> = emptyList(),        // e.g., "Cramps", "Headache"
    val vaginalDischarge: List<String> = emptyList(),// e.g., "Clear", "Sticky", "Creamy"
    val physicalActivity: List<String> = emptyList(),// e.g., "Yoga", "Running"
    val pillsTaken: List<String> = emptyList(),      // e.g., "Painkiller", "Contraceptive"
    val waterIntake: Double? = null,              // Liters of water (e.g., 1.5)
    val weight: Double? = null,                   // Weight in kg
    val notes: String? = null
)