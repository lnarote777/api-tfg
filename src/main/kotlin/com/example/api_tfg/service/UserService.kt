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
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * Servicio para gestionar usuarios.
 *
 * Implementa UserDetailsService para integración con Spring Security.
 *
 * @property userRepository Repositorio para operaciones con usuarios.
 * @property passwordEncoder Codificador de contraseñas para seguridad.
 */
@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
): UserDetailsService {

    /**
     * Carga un usuario por su nombre de usuario para autenticación.
     *
     * @param username Nombre de usuario.
     * @return Detalles del usuario para Spring Security.
     * @throws UnauthorizedException Si el usuario no existe.
     */
    override fun loadUserByUsername(username: String?): UserDetails {
        val user: UserEntity = userRepository
            .findByUsername(username!!)
            .orElseThrow {
                UnauthorizedException("$username-+ no existente")
            }

        val authorities = listOf(SimpleGrantedAuthority("ROLE_${user.roles.uppercase()}"))

        return User.builder()
            .username(user.username)
            .password(user.password)
            .authorities(authorities)
            .build()
    }

    /**
     * Registra un nuevo usuario.
     *
     * Verifica que el usuario no exista, valida las credenciales y guarda el usuario en la base de datos.
     *
     * @param userRegisterDTO Datos para registrar el usuario.
     * @return DTO del usuario registrado.
     * @throws UserExistException Si ya existe un usuario con el mismo email.
     * @throws BadRequestException Si las credenciales no cumplen con los requisitos.
     */
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

    /**
     * Obtiene todos los usuarios registrados.
     *
     * @return Lista de DTOs con la información de todos los usuarios.
     */
    fun getAllUsers(): List<UserDTO> {
        val users = userRepository.findAll()
        val usersDTO = mutableListOf<UserDTO>()
        for (user in users){
            val usuarioDTO = DTOMapper.userEntityToDTO(user)
            usersDTO.add(usuarioDTO)
        }
        return usersDTO
    }

    /**
     * Obtiene un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario.
     * @return DTO con la información del usuario.
     * @throws BadRequestException Si no se encuentra el usuario.
     */
    fun getUserByUsername(username: String): UserDTO {
        val user = userRepository.findByUsername(username).orElseThrow{BadRequestException("$username-- no existente")}
        val userDTO = DTOMapper.userEntityToDTO(user)
        return userDTO
    }

    /**
     * Obtiene un usuario por su email.
     *
     * @param email Email del usuario.
     * @return DTO con la información del usuario.
     * @throws BadRequestException Si no se encuentra el usuario.
     */
    fun getUserByEmail(email: String): UserDTO {
        val user = userRepository.findUserBy_id(email).orElseThrow{BadRequestException("$email-- no existente")}
        val userDTO = DTOMapper.userEntityToDTO(user)
        return userDTO
    }

    /**
     * Elimina un usuario por su email.
     *
     * @param email Email del usuario a eliminar.
     * @return DTO del usuario eliminado.
     * @throws NotFoundException Si no se encuentra el usuario.
     */
    fun deleteUser(email: String): UserDTO {
        val user = userRepository.findUserBy_id(email).orElseThrow{ NotFoundException("No se encontró al usuario con email $email.") }
        userRepository.delete(user)
        return DTOMapper.userEntityToDTO(user)
    }

    /**
     * Actualiza la información de un usuario.
     *
     * Si la contraseña es diferente y no está vacía, la encripta y actualiza.
     * También actualiza el nombre de usuario y el objetivo (goal).
     *
     * @param userInsertDTO Datos actualizados del usuario.
     * @return DTO con la información actualizada.
     * @throws NotFoundException Si no se encuentra el usuario.
     */
    fun updateUser(userInsertDTO: UserUpdateDTO): UserDTO {
        val user: UserEntity = userRepository.findUserBy_id(userInsertDTO.email)
            .orElseThrow { NotFoundException("No se encontró al usuario con email ${userInsertDTO.email}.") }

        if (userInsertDTO.password.isNotBlank() && userInsertDTO.password != user.password) {
            user.password = passwordEncoder.encode(userInsertDTO.password)
        }

        user.username = userInsertDTO.username
        user.goal = userInsertDTO.goal

        userRepository.save(user)
        return DTOMapper.userEntityToDTO(user)
    }

    /**
     * Valida las credenciales y datos proporcionados para registrar un usuario.
     *
     * @param userRegisterDTO Datos de registro del usuario.
     * @throws BadRequestException Si algún campo no cumple los requisitos.
     */
    private fun comprobarCredenciales(userRegisterDTO: UserRegisterDTO){
        if (userRegisterDTO.email.isBlank() || !userRegisterDTO.email.contains("@")) throw BadRequestException("Formato de email inválido")
        if (userRegisterDTO.password.isBlank() || userRegisterDTO.password.length < 6 ) throw BadRequestException("Formato de password inválido.")
        if (userRegisterDTO.name.isBlank()) throw BadRequestException("Formato de nombre inválido.")
        if (userRegisterDTO.passwordRepeat.isBlank() || userRegisterDTO.password != userRegisterDTO.passwordRepeat) throw BadRequestException("Las contraseñas no coinciden.")
        if (!userRegisterDTO.birthDate.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) throw BadRequestException("Formato de fecha inválido. Debe ser YYYY-MM-DD.")
    }

}