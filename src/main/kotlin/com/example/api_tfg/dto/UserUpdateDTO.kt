package com.example.api_tfg.dto

import com.example.api_tfg.model.Goal

data class UserUpdateDTO (
    val email: String,
    val name: String,
    var username: String,
    var password: String,
    var weight: Double,
    var height: Double,
    var goal: Goal
)
