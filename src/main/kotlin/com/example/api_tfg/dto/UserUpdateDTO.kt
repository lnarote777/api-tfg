package com.example.api_tfg.dto

import com.example.api_tfg.model.Goal

data class UserUpdateDTO (
    val email: String,
    var username: String,
    var password: String,
    var goal: Goal
)
