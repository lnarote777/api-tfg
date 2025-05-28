package com.example.api_tfg.dto

import com.example.api_tfg.model.Goal

class UserDTO (
    val email: String,
    val username: String,
    val name: String,
    val goal: Goal
)