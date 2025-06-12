package com.example.api_tfg.model

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

/**
 * Entidad que representa a un usuario en la base de datos.
 *
 * @property _id Identificador único del usuario, que en este caso es su email.
 * @property name Nombre completo del usuario.
 * @property username Nombre de usuario para login o visualización.
 * @property password Contraseña cifrada del usuario.
 * @property roles Rol asignado al usuario, por defecto "USER", puede ser "PREMIUM".
 * @property birthDate Fecha de nacimiento del usuario en formato String.
 * @property registrationDate Fecha en que el usuario se registró en la aplicación.
 * @property weight Peso actual del usuario (en kg).
 * @property goal Objetivo personal del usuario respecto a su salud reproductiva.
 */
@Document("Users")
data class UserEntity(
    @BsonId
    val _id: String, //email
    val name: String,
    var username: String,
    var password: String,
    var roles: String = "USER", //Premium or user
    val birthDate: String,
    val registrationDate: Date,
    var weight: Double,
    var goal: Goal
)

/**
 * Enum que representa los objetivos personales del usuario relacionados con salud reproductiva.
 */
enum class Goal {
    GET_PREGNANT, TRACK_PERIOD, AVOID_PREGNANCY
}