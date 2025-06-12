package com.example.api_tfg.dto

import com.example.api_tfg.model.Goal

/**
 * Data Transfer Object para representar la información básica de un usuario.
 *
 * @property email Correo electrónico del usuario, que también actúa como identificador único.
 * @property username Nombre de usuario único dentro de la aplicación.
 * @property name Nombre completo o mostrado del usuario.
 * @property goal Objetivo del usuario, representado por el enum [Goal].
 */
class UserDTO (
    val email: String,
    val username: String,
    val name: String,
    val goal: Goal
)