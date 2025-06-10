package com.example.api_tfg.pruebasUnitarias.controllers

import com.example.api_tfg.controller.UserController
import com.example.api_tfg.dto.UserDTO
import com.example.api_tfg.dto.UserLoginDTO
import com.example.api_tfg.dto.UserRegisterDTO
import com.example.api_tfg.dto.UserUpdateDTO
import com.example.api_tfg.error.exception.BadRequestException
import com.example.api_tfg.error.exception.UnauthorizedException
import com.example.api_tfg.model.Goal
import com.example.api_tfg.service.TokenService
import com.example.api_tfg.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication

class UserControllerTest {

    private lateinit var userController: UserController
    private val userService: UserService = mock(UserService::class.java)
    private val tokenService: TokenService = mock(TokenService::class.java)
    private val authenticationManager: AuthenticationManager = mock(AuthenticationManager::class.java)
    private val httpRequest: HttpServletRequest = mock(HttpServletRequest::class.java)

    @BeforeEach
    fun setUp() {
        userController = UserController(authenticationManager, tokenService, userService)
    }

    @Test
    fun `login should return token when credentials are correct`() {
        val loginDTO = UserLoginDTO(username = "user", password = "pass")
        val authentication: Authentication = mock(Authentication::class.java)

        `when`(authenticationManager.authenticate(any())).thenReturn(authentication)
        `when`(tokenService.generarToken(authentication)).thenReturn("token123")

        val response = userController.login(loginDTO)

        assertNotNull(response)
        assertEquals(HttpStatus.OK, response?.statusCode)
        assertTrue(response?.body is Map<*, *>)
        val body = response?.body as Map<*, *>
        assertEquals("token123", body["token"])
    }

    @Test
    fun `login should throw BadRequestException if user is null`() {
        val exception = assertThrows(BadRequestException::class.java) {
            userController.login(null)
        }
        assertEquals("Bad Request Exception (400). El parámetro no puede estar vacío.", exception.message)
    }

    @Test
    fun `login should throw UnauthorizedException when authentication fails`() {
        val loginDTO = UserLoginDTO(username = "user", password = "wrongpass")

        `when`(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException("Credenciales incorrectas"))

        val exception = assertThrows(UnauthorizedException::class.java) {
            userController.login(loginDTO)
        }
        assertEquals("Not authorized exception (401). Credenciales incorrectas", exception.message)    }

    @Test
    fun `insert should return created user`() {
        val registerDTO = UserRegisterDTO(
            username = "newuser", password = "pass",
            email = "newuser@example.com",
            passwordRepeat = "pass",
            name = "newuser",
            birthDate = "2000-07-09",
            goal = Goal.TRACK_PERIOD
        )
        val userDTO = UserDTO(username = "newuser", email = "newuser@example.com", name = "newuser", goal = Goal.TRACK_PERIOD)

        `when`(userService.registerUser(registerDTO)).thenReturn(userDTO)

        val response = userController.insert(httpRequest, registerDTO)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(userDTO, response.body)
    }

    @Test
    fun `getAllUsers should return list of users`() {
        val users = listOf(
            UserDTO(username = "user1", email = "user1@example.com", name = "user1", goal = Goal.TRACK_PERIOD),
            UserDTO(username = "user2", email = "user2@example.com", name = "user2", goal = Goal.TRACK_PERIOD)
        )

        `when`(userService.getAllUsers()).thenReturn(users)

        val response = userController.getAllUsers(httpRequest)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(users, response.body)
    }

    @Test
    fun `getUserByUsername should return user`() {
        val userDTO = UserDTO(username = "user1", email = "user1@example.com", name = "user1", goal = Goal.TRACK_PERIOD)

        `when`(userService.getUserByUsername("user1")).thenReturn(userDTO)

        val response = userController.getUserByUsername("user1", httpRequest)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(userDTO, response.body)
    }

    @Test
    fun `deleteByEmail should return deleted user`() {
        val deletedUser = UserDTO(username = "userToDelete", email = "delete@example.com", name = "delete", goal = Goal.TRACK_PERIOD)

        `when`(userService.deleteUser("delete@example.com")).thenReturn(deletedUser)

        val response = userController.deleteByEmail("delete@example.com", httpRequest)

        assertEquals(HttpStatus.OK, response?.statusCode)
        assertEquals(deletedUser, response?.body)
    }

    @Test
    fun `update should return updated user`() {
        val updateDTO = UserUpdateDTO(
            username = "user1",
            email = "user1@example.com",
            goal = Goal.TRACK_PERIOD,
            password = "7654985"
        )
        val updatedUser = UserDTO(username = "user1", email = "user1@example.com", name = "user1", goal = Goal.TRACK_PERIOD)

        `when`(userService.updateUser(updateDTO)).thenReturn(updatedUser)

        val response = userController.update(updateDTO, httpRequest)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updatedUser, response.body)
    }
}
