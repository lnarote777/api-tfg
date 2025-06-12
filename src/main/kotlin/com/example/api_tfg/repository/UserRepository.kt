package com.example.api_tfg.repository

import com.example.api_tfg.model.UserEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repositorio para gestionar la persistencia de usuarios (`UserEntity`) en MongoDB.
 */
@Repository
interface UserRepository: MongoRepository<UserEntity, String> {
    /**
     * Busca un usuario por su email, que se usa como identificador (_id).
     *
     * @param email Email del usuario.
     * @return Optional que contiene el usuario si se encuentra, o vacío si no.
     */
    fun findUserBy_id(email: String): Optional<UserEntity>
    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario.
     * @return Optional que contiene el usuario si se encuentra, o vacío si no.
     */
    fun findByUsername(username: String): Optional<UserEntity>
}