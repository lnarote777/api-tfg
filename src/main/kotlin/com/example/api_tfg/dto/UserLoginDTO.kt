package com.example.api_tfg.dto

/**
 * Data Transfer Object para la autenticación de un usuario.
 *
 * Contiene las credenciales necesarias para iniciar sesión.
 *
 * @property username Nombre de usuario o identificador único para login.
 * @property password Contraseña asociada al usuario.
 */
data class UserLoginDTO (
    val username: String,
    val password: String
)