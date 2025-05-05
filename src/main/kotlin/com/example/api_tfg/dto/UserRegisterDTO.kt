package com.example.api_tfg.dto

import com.example.api_tfg.model.Goal

data class UserRegisterDTO (
    val email: String,
    val username: String,
    val password: String,
    val passwordRepeat: String,
    val name: String,
    val birthDate: String,
    val goal: Goal = Goal.TRACK_PERIOD //por defecto, luego se cambia
)