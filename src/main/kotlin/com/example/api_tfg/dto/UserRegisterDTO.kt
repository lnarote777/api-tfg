package com.example.api_tfg.dto

import com.example.api_tfg.model.Goal

/**
 * Data Transfer Object para el registro de un nuevo usuario.
 *
 * Contiene los datos necesarios para crear una cuenta de usuario.
 *
 * @property email Correo electrónico único del usuario.
 * @property username Nombre de usuario elegido.
 * @property password Contraseña para la cuenta.
 * @property passwordRepeat Repetición de la contraseña para verificación.
 * @property name Nombre completo del usuario.
 * @property birthDate Fecha de nacimiento del usuario en formato String.
 * @property goal Objetivo principal del usuario, por defecto `TRACK_PERIOD`.
 */
data class UserRegisterDTO (
    val email: String,
    val username: String,
    val password: String,
    val passwordRepeat: String,
    val name: String,
    val birthDate: String,
    val goal: Goal = Goal.TRACK_PERIOD //por defecto, luego se cambia
)