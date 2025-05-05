package com.example.api_tfg.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("OptionItem")
data class OptionItem(
    @Id
    val id: String? = null,
    val category: OptionCategory,
    val value: String,              // "Dolor de cabeza"
    val icon: String,               // Emoji o URL: "🤕" o "/icons/headache.png"
    val isDefault: Boolean = true,
    val userId: String? = null
)

enum class OptionCategory {
    SYMPTOM,
    MOOD,
    SEXUAL_ACTIVITY,
    VAGINAL_DISCHARGE,
    PHYSICAL_ACTIVITY,
    PILL
}