package com.example.api_tfg.model

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document

@Document("Subscriptions")
data class Subscription(
    @BsonId
    val id: String? = null,
    val email: String,
    val type:SubscriptionType
)

enum class SubscriptionType {
    MONTHLY, ONE_TIME
}