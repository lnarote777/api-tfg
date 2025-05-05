package com.example.api_tfg.model

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document("Users")
data class UserEntity(
    @BsonId
    val _id: String, //email
    val name: String,
    var username: String,
    var password: String,
    var roles: String, //Premium or user
    val birthDate: String,
    val registrationDate: Date,
    var weight: Double,
    var height: Double,
    var goal: Goal
)

enum class Goal {
    GET_PREGNANT, TRACK_PERIOD, AVOID_PREGNANCY
}