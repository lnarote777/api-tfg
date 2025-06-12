package com.example.api_tfg.dto

import com.example.api_tfg.model.UserEntity
import java.time.Instant
import java.util.*
/**
 * Objeto para mapear entre DTOs y entidades de usuario.
 */
object DTOMapper {

    /**
     * Convierte un [UserRegisterDTO] a una entidad [UserEntity].
     *
     * @param userDTO DTO con los datos de registro del usuario.
     * @return Entidad [UserEntity] con los datos correspondientes y valores por defecto para algunos campos.
     */
    fun userDTOToEntity(userDTO: UserRegisterDTO): UserEntity {
        return UserEntity(
            _id = userDTO.email,
            name = userDTO.name,
            username = userDTO.username,
            password = userDTO.password,
            birthDate = userDTO.birthDate,
            goal = userDTO.goal,
            roles = "USER",
            weight = 0.0,
            registrationDate = Date.from(Instant.now())
        )
    }

    /**
     * Convierte una entidad [UserEntity] a un DTO [UserDTO].
     *
     * @param user Entidad de usuario.
     * @return DTO con los datos básicos de usuario para transferir.
     */
    fun userEntityToDTO(user: UserEntity): UserDTO {
        return UserDTO(
            email = user._id,
            username = user.username,
            name = user.name,
            goal = user.goal
        )
    }

}