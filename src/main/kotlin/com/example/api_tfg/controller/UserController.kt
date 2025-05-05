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

@Controller
@RequestMapping("/users")
class UserController {

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var tokenService: TokenService

    @Autowired
    private lateinit var userService: UserService

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

    @PostMapping("/register")
    fun insert(
        httpRequest: HttpServletRequest,
        @RequestBody userInsertDTO: UserRegisterDTO
    ) : ResponseEntity<UserDTO>{
        val user = userService.registerUser(userInsertDTO)
        return ResponseEntity(user, HttpStatus.CREATED)
    }

    @GetMapping("/list-users")
    fun getAllUsers(
        httpRequest: HttpServletRequest
    ): ResponseEntity<List<UserDTO>> {
        val users = userService.getAllUsers()
        return ResponseEntity(users, HttpStatus.OK )
    }

    @DeleteMapping("/delete/{email}")
    fun deleteByEmail(
        @PathVariable email: String,
        httpRequest: HttpServletRequest
    ) : ResponseEntity<UserDTO>? {
        val user = userService.deleteUser(email)
        return ResponseEntity(user, HttpStatus.OK)
    }

    @PutMapping("/update")
    fun update(
        @RequestBody usuarioUpdated: UserUpdateDTO,
        httpRequest: HttpServletRequest,
    ): ResponseEntity<UserDTO>{
        val user = userService.updateUser(usuarioUpdated)
        return ResponseEntity(user, HttpStatus.OK)
    }

}