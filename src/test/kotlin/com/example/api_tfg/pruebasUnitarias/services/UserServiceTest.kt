package com.example.api_tfg.service

import com.example.api_tfg.dto.UserRegisterDTO
import com.example.api_tfg.dto.UserUpdateDTO
import com.example.api_tfg.error.exception.BadRequestException
import com.example.api_tfg.error.exception.NotFoundException
import com.example.api_tfg.error.exception.UnauthorizedException
import com.example.api_tfg.error.exception.UserExistException
import com.example.api_tfg.model.Goal
import com.example.api_tfg.model.UserEntity
import com.example.api_tfg.repository.UserRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

internal class UserServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        passwordEncoder = mockk()
        userService = UserService(userRepository, passwordEncoder)
    }

    @Test
    fun `loadUserByUsername returns UserDetails when user exists`() {
        val email = "test@example.com"
        val userEntity = UserEntity(
            _id = email,
            name = "Test User",
            username = "testuser",
            password = "encodedPassword",
            roles = "USER",
            birthDate = "1990-01-01",
            registrationDate = Date(),
            weight = 65.0,
            goal = Goal.TRACK_PERIOD
        )

        every { userRepository.findByUsername(userEntity.username) } returns Optional.of(userEntity)

        val userDetails: UserDetails = userService.loadUserByUsername(userEntity.username)

        assertEquals(userEntity.username, userDetails.username)
        assertEquals(userEntity.password, userDetails.password)
        assertTrue(userDetails.authorities.any { it.authority == "ROLE_${userEntity.roles.uppercase()}" })

        verify { userRepository.findByUsername(userEntity.username) }
    }

    @Test
    fun `loadUserByUsername throws UnauthorizedException when user not found`() {
        val username = "nonexistent"

        every { userRepository.findByUsername(username) } returns Optional.empty()

        val exception = assertThrows(UnauthorizedException::class.java) {
            userService.loadUserByUsername(username)
        }

        assertTrue(exception.message!!.contains(username))
        verify { userRepository.findByUsername(username) }
    }

    @Test
    fun `registerUser throws UserExistException if user with email exists`() {
        val dto = UserRegisterDTO(
            email = "existing@example.com",
            username = "user1",
            password = "password123",
            passwordRepeat = "password123",
            name = "User One",
            birthDate = "1995-05-05"
        )

        every { userRepository.findUserBy_id(dto.email) } returns Optional.of(
            UserEntity(
                dto.email,
                dto.name,
                dto.username,
                "pass",
                "USER",
                dto.birthDate,
                Date(),
                60.0,
                Goal.TRACK_PERIOD
            )
        )

        val ex = assertThrows(UserExistException::class.java) {
            userService.registerUser(dto)
        }

        assertEquals("User Exist (400). Usuario existente.", ex.message)
        verify { userRepository.findUserBy_id(dto.email) }
        confirmVerified(userRepository)
    }

    @Test
    fun `registerUser throws BadRequestException if credentials invalid`() {
        val dto = UserRegisterDTO(
            email = "invalidemail",
            username = "user1",
            password = "123",
            passwordRepeat = "1234",
            name = "",
            birthDate = "05-05-1995"
        )
        every { userRepository.findUserBy_id("invalidemail") } returns Optional.empty()

        val ex = assertThrows(BadRequestException::class.java) {
            userService.registerUser(dto)
        }

    }

    @Test
    fun `registerUser saves user and returns UserDTO on success`() {
        val dto = UserRegisterDTO(
            email = "newuser@example.com",
            username = "newuser",
            password = "securepass",
            passwordRepeat = "securepass",
            name = "New User",
            birthDate = "1995-05-05"
        )

        every { userRepository.findUserBy_id(dto.email) } returns Optional.empty()
        every { passwordEncoder.encode(dto.password) } returns "encodedpass"

        // Mock save just returns the passed user entity (simulate DB save)
        every { userRepository.save(any()) } answers { firstArg() }

        val result = userService.registerUser(dto)

        assertEquals(dto.email, result.email)
        assertEquals(dto.username, result.username)
        assertEquals(dto.name, result.name)
        assertEquals(dto.goal, result.goal)

        verifySequence {
            userRepository.findUserBy_id(dto.email)
            passwordEncoder.encode(dto.password)
            userRepository.save(any())
        }
    }

    @Test
    fun `getAllUsers returns list of UserDTO`() {
        val userList = listOf(
            UserEntity("a@example.com", "Name A", "userA", "passA", "USER", "1990-01-01", Date(), 60.0, Goal.TRACK_PERIOD),
            UserEntity("b@example.com", "Name B", "userB", "passB", "PREMIUM", "1991-01-01", Date(), 65.0, Goal.GET_PREGNANT)
        )

        every { userRepository.findAll() } returns userList

        val usersDTO = userService.getAllUsers()

        assertEquals(2, usersDTO.size)
        assertTrue(usersDTO.any { it.email == "a@example.com" && it.username == "userA" })
        assertTrue(usersDTO.any { it.email == "b@example.com" && it.username == "userB" })

        verify { userRepository.findAll() }
    }

    @Test
    fun `getUserByUsername returns UserDTO if found`() {
        val username = "userA"
        val user = UserEntity(
            "a@example.com",
            "Name A",
            username,
            "passA",
            "USER",
            "1990-01-01",
            Date(),
            60.0,
            Goal.TRACK_PERIOD
        )

        every { userRepository.findByUsername(username) } returns Optional.of(user)

        val userDTO = userService.getUserByUsername(username)

        assertEquals(username, userDTO.username)
        assertEquals(user._id, userDTO.email)

        verify { userRepository.findByUsername(username) }
    }

    @Test
    fun `getUserByUsername throws BadRequestException if not found`() {
        val username = "missingUser"
        every { userRepository.findByUsername(username) } returns Optional.empty()

        val ex = assertThrows(BadRequestException::class.java) {
            userService.getUserByUsername(username)
        }
        assertTrue(ex.message!!.contains(username))
        verify { userRepository.findByUsername(username) }
    }

    @Test
    fun `getUserByEmail returns UserDTO if found`() {
        val email = "email@example.com"
        val user = UserEntity(
            email,
            "Name",
            "username",
            "pass",
            "USER",
            "1990-01-01",
            Date(),
            70.0,
            Goal.AVOID_PREGNANCY
        )

        every { userRepository.findUserBy_id(email) } returns Optional.of(user)

        val userDTO = userService.getUserByEmail(email)

        assertEquals(email, userDTO.email)
        assertEquals(user.username, userDTO.username)

        verify { userRepository.findUserBy_id(email) }
    }

    @Test
    fun `getUserByEmail throws BadRequestException if not found`() {
        val email = "missing@example.com"
        every { userRepository.findUserBy_id(email) } returns Optional.empty()

        val ex = assertThrows(BadRequestException::class.java) {
            userService.getUserByEmail(email)
        }
        assertTrue(ex.message!!.contains(email))
        verify { userRepository.findUserBy_id(email) }
    }

    @Test
    fun `deleteUser deletes user and returns UserDTO`() {
        val email = "del@example.com"
        val user = UserEntity(
            email,
            "Name",
            "username",
            "pass",
            "USER",
            "1990-01-01",
            Date(),
            70.0,
            Goal.AVOID_PREGNANCY)

        every { userRepository.findUserBy_id(email) } returns Optional.of(user)
        every { userRepository.delete(user) } just Runs

        val userDTO = userService.deleteUser(email)

        assertEquals(email, userDTO.email)
        verifySequence {
            userRepository.findUserBy_id(email)
            userRepository.delete(user)
        }
    }

    @Test
    fun `deleteUser throws NotFoundException if user not found`() {
        val email = "missingdel@example.com"
        every { userRepository.findUserBy_id(email) } returns Optional.empty()

        val ex = assertThrows(NotFoundException::class.java) {
            userService.deleteUser(email)
        }
        assertTrue(ex.message!!.contains(email))
        verify { userRepository.findUserBy_id(email) }
    }

    @Test
    fun `updateUser updates and returns UserDTO`() {
        val email = "update@example.com"
        val oldUser = UserEntity(
            email,
            "Old Name",
            "oldUsername",
            "oldpass",
            "USER",
            "1990-01-01",
            Date(), 70.0,
            Goal.TRACK_PERIOD
        )
        val updateDTO = UserUpdateDTO(email, "newUsername", "newpass", Goal.GET_PREGNANT)

        every { userRepository.findUserBy_id(email) } returns Optional.of(oldUser)
        every { passwordEncoder.encode(updateDTO.password) } returns "encodedNewPass"
        every { userRepository.save(any()) } answers { firstArg() }

        val updatedUserDTO = userService.updateUser(updateDTO)

        assertEquals(updateDTO.email, updatedUserDTO.email)
        assertEquals(updateDTO.username, updatedUserDTO.username)
        assertEquals(updateDTO.goal, updatedUserDTO.goal)
        assertEquals("encodedNewPass", oldUser.password) // password updated and encoded

        verifySequence {
            userRepository.findUserBy_id(email)
            passwordEncoder.encode(updateDTO.password)
            userRepository.save(oldUser)
        }
    }

    @Test
    fun `updateUser does not change password if blank`() {
        val email = "updateblank@example.com"
        val oldUser = UserEntity(
            email, "Old Name",
            "oldUsername",
            "oldpass",
            "USER",
            "1990-01-01",
            Date(),
            70.0,
            Goal.TRACK_PERIOD
        )
        val updateDTO = UserUpdateDTO(email, "newUsername", "", Goal.GET_PREGNANT)

        every { userRepository.findUserBy_id(email) } returns Optional.of(oldUser)
        every { userRepository.save(any<UserEntity>()) } answers { firstArg() }

        val updatedUserDTO = userService.updateUser(updateDTO)

        assertEquals(updateDTO.email, updatedUserDTO.email)
        assertEquals(updateDTO.username, updatedUserDTO.username)
        assertEquals(updateDTO.goal, updatedUserDTO.goal)
        assertEquals("oldpass", oldUser.password) // password unchanged

        verifySequence {
            userRepository.findUserBy_id(email)
            userRepository.save(oldUser)
        }
        verify(exactly = 0) { passwordEncoder.encode(any()) }
    }
}
