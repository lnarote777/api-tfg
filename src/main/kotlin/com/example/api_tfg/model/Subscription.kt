package com.example.api_tfg.model

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Representa una suscripción de un usuario a un plan o servicio.
 *
 * @property id Identificador único de la suscripción (generado por la base de datos).
 * @property email Correo electrónico del usuario asociado a la suscripción.
 * @property type Tipo de suscripción (por ejemplo, mensual o única).
 */
@Document("Subscriptions")
data class Subscription(
    @BsonId
    val id: String? = null,
    val email: String,
    val type:SubscriptionType
)

/**
 * Enum que representa los diferentes tipos de suscripciones disponibles.
 */
enum class SubscriptionType {
    MONTHLY, ONE_TIME
}