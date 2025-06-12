package com.example.api_tfg.controller

import com.example.api_tfg.service.UserService
import com.example.api_tfg.dto.UserDTO
import com.example.api_tfg.dto.UserLoginDTO
import com.example.api_tfg.dto.UserRegisterDTO
import com.example.api_tfg.dto.UserUpdateDTO
import com.example.api_tfg.error.exception.BadRequestException
import com.example.api_tfg.error.exception.UnauthorizedException
import com.example.api_tfg.service.TokenService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

/**
 * Controlador REST para la gestión de usuarios.
 *
 * Proporciona endpoints para autenticación (login), registro,
 * consulta, actualización y eliminación de usuarios.
 *
 * @property authenticationManager Componente para la autenticación de usuarios.
 * @property tokenService Servicio para generación de tokens JWT.
 * @property userService Servicio con la lógica de negocio de usuarios.
 */
@Controller
@RequestMapping("/users")
class UserController(
    private val authenticationManager: AuthenticationManager,
    private val tokenService: TokenService,
    private val userService: UserService
) {

    /**
     * Endpoint para el inicio de sesión de usuarios.
     *
     * @param user DTO con username y password para autenticación.
     * @throws BadRequestException si el parámetro user es nulo.
     * @throws UnauthorizedException si las credenciales son incorrectas.
     * @return ResponseEntity con un mapa que contiene el token JWT generado.
     */
    @PostMapping("/login")
    fun login(@RequestBody user: UserLoginDTO?) : ResponseEntity<Any>? {
        if(user == null){
            throw BadRequestException("El parámetro no puede estar vacío.")
        }

        val authentication: Authentication = try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(user.username, user.password)
            )
        } catch (e: AuthenticationException) {
            throw UnauthorizedException("Credenciales incorrectas")
        }

        val token = tokenService.generarToken(authentication)
        return ResponseEntity(mapOf("token" to token), HttpStatus.OK)
    }

    /**
     * Endpoint para registrar un nuevo usuario.
     *
     * @param httpRequest Solicitud HTTP entrante.
     * @param userInsertDTO DTO con los datos necesarios para registrar un usuario.
     * @return ResponseEntity con el usuario creado.
     */
    @PostMapping("/register")
    fun insert(
        httpRequest: HttpServletRequest,
        @RequestBody userInsertDTO: UserRegisterDTO
    ) : ResponseEntity<UserDTO>{
        val user = userService.registerUser(userInsertDTO)
        return ResponseEntity(user, HttpStatus.CREATED)
    }

    /**
     * Obtiene la lista de todos los usuarios registrados.
     *
     * @param httpRequest Solicitud HTTP entrante.
     * @return ResponseEntity con la lista de usuarios en formato DTO.
     */
    @GetMapping("/list-users")
    fun getAllUsers(
        httpRequest: HttpServletRequest
    ): ResponseEntity<List<UserDTO>> {
        val users = userService.getAllUsers()
        return ResponseEntity(users, HttpStatus.OK )
    }

    /**
     * Obtiene un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario a buscar.
     * @param httpRequest Solicitud HTTP entrante.
     * @return ResponseEntity con el usuario encontrado.
     */
    @GetMapping("/{username}")
    fun getUserByUsername(
        @PathVariable username: String,
        httpRequest: HttpServletRequest
    ): ResponseEntity<UserDTO> {
        val user = userService.getUserByUsername(username)
        return ResponseEntity(user, HttpStatus.OK)
    }

    /**
     * Elimina un usuario identificado por su email.
     *
     * @param email Email del usuario a eliminar.
     * @param httpRequest Solicitud HTTP entrante.
     * @return ResponseEntity con el usuario eliminado.
     */
    @DeleteMapping("/delete")
    fun deleteByEmail(
        @RequestParam email: String,
        httpRequest: HttpServletRequest
    ) : ResponseEntity<UserDTO>? {
        val user = userService.deleteUser(email)
        return ResponseEntity(user, HttpStatus.OK)
    }

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param usuarioUpdated DTO con los datos actualizados del usuario.
     * @param httpRequest Solicitud HTTP entrante.
     * @return ResponseEntity con el usuario actualizado.
     */
    @PutMapping("/update")
    fun update(
        @RequestBody usuarioUpdated: UserUpdateDTO,
        httpRequest: HttpServletRequest,
    ): ResponseEntity<UserDTO>{
        val user = userService.updateUser(usuarioUpdated)
        return ResponseEntity(user, HttpStatus.OK)
    }

}