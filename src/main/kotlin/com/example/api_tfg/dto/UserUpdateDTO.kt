package com.example.api_tfg.dto

import com.example.api_tfg.model.Goal

/**
 * Data Transfer Object para actualizar los datos de un usuario existente.
 *
 * @property email Correo electrónico del usuario (identificador único).
 * @property username Nuevo nombre de usuario.
 * @property password Nueva contraseña.
 * @property goal Nuevo objetivo del usuario.
 */
data class UserUpdateDTO (
    val email: String,
    var username: String,
    var password: String,
    var goal: Goal
)
