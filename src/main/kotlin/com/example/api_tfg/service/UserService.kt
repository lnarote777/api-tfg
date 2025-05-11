package com.example.api_tfg.service

import com.example.api_tfg.dto.DTOMapper
import com.example.api_tfg.dto.UserDTO
import com.example.api_tfg.dto.UserRegisterDTO
import com.example.api_tfg.dto.UserUpdateDTO
import com.example.api_tfg.error.exception.BadRequestException
import com.example.api_tfg.error.exception.NotFoundException
import com.example.api_tfg.error.exception.UnauthorizedException
import com.example.api_tfg.error.exception.UserExistException
import com.example.api_tfg.model.UserEntity
import com.example.api_tfg.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService: UserDetailsService {
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var userRepository: UserRepository

    override fun loadUserByUsername(username: String?): UserDetails {
        val user: UserEntity = userRepository
            .findByUsername(username!!)
            .orElseThrow {
                UnauthorizedException("$username-+ no existente")
            }

        return User.builder()
            .username(user.username)
            .password(user.password)
            .build()
    }

    fun registerUser(userRegisterDTO: UserRegisterDTO): UserDTO {
        val exist = userRepository.findUserBy_id(userRegisterDTO.email)
        if (exist.isPresent) {
            throw UserExistException("Usuario existente.")
        }

        comprobarCredenciales(userRegisterDTO)

        val user: UserEntity = DTOMapper.userDTOToEntity(userRegisterDTO)
        user.password = passwordEncoder.encode(user.password)

        userRepository.save(user)
        val userDTO = DTOMapper.userEntityToDTO(user)

        return userDTO
    }

    fun getAllUsers(): List<UserDTO> {
        val users = userRepository.findAll()
        val usersDTO = mutableListOf<UserDTO>()
        for (user in users){
            val usuarioDTO = DTOMapper.userEntityToDTO(user)
            usersDTO.add(usuarioDTO)
        }
        return usersDTO
    }

    fun getUserByUsername(username: String): UserDTO {
        val user = userRepository.findByUsername(username).orElseThrow{BadRequestException("$username-- no existente")}
        val userDTO = DTOMapper.userEntityToDTO(user)
        return userDTO
    }

    fun deleteUser(email: String): UserDTO {
        val user = userRepository.findUserBy_id(email).orElseThrow{ NotFoundException("No se encontró al usuario con email $email.") }
        userRepository.delete(user)
        return DTOMapper.userEntityToDTO(user)
    }

    fun updateUser(userInsertDTO: UserUpdateDTO): UserDTO {
        val user: UserEntity = userRepository.findUserBy_id(userInsertDTO.email).orElseThrow { NotFoundException("No se encontró al usuario con email ${userInsertDTO.email}.") }
        user.password = passwordEncoder.encode(userInsertDTO.password)
        user.username = userInsertDTO.username
        user.goal = userInsertDTO.goal
        user.height = userInsertDTO.height
        user.weight = userInsertDTO.weight
        userRepository.save(user)
        val userDTO = DTOMapper.userEntityToDTO(user)
        return userDTO
    }


    private fun comprobarCredenciales(userRegisterDTO: UserRegisterDTO){
        if (userRegisterDTO.email.isBlank() || !userRegisterDTO.email.contains("@")) throw BadRequestException("Formato de email inválido")
        if (userRegisterDTO.password.isBlank() || userRegisterDTO.password.length < 6 ) throw BadRequestException("Formato de password inválido.")
        if (userRegisterDTO.name.isBlank()) throw BadRequestException("Formato de nombre inválido.")
        if (userRegisterDTO.passwordRepeat.isBlank() || userRegisterDTO.password != userRegisterDTO.passwordRepeat) throw BadRequestException("Las contraseñas no coinciden.")
        if (!userRegisterDTO.birthDate.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) throw BadRequestException("Formato de fecha inválido. Debe ser YYYY-MM-DD.")
    }

}