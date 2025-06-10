package com.example.api_tfg.pruebasUnitarias.controllers

import com.example.api_tfg.dto.DTOMapper
import com.example.api_tfg.dto.DailyLogDTO
import com.example.api_tfg.model.*
import com.example.api_tfg.service.DailyLogService
import com.example.api_tfg.service.UserService
import com.example.api_tfg.testConfiguration.DailyLogControllerTestConfig
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@Import(DailyLogControllerTestConfig::class)
class DailyLogControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var dailyLogService: DailyLogService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val email = "test@example.com"
    private val logId = "60f5c5a1a9f3c3a0f8b45678"
    private val testDate = "2025-06-10"

    private fun sampleDTO() = DailyLogDTO(
        date = testDate,
        hasMenstruation = true,
        menstrualFlow = MenstrualFlowLevel.HEAVY,
        sexualActivity = mutableListOf("protected"),
        mood = mutableListOf("happy"),
        symptoms = mutableListOf("cramps"),
        vaginalDischarge = mutableListOf("clear"),
        physicalActivity = mutableListOf("yoga"),
        pillsTaken = mutableListOf("ibuprofen"),
        waterIntake = 2.0,
        weight = 55.5,
        notes = "Feeling good"
    )

    private fun sampleUser() = UserEntity(
        _id = email,
        name = "Test User",
        username = "testuser",
        password = "encrypted",
        birthDate = "2000-01-01",
        registrationDate = Date(),
        weight = 55.5,
        goal = Goal.TRACK_PERIOD
    )

    private fun sampleLog() = DailyLog(
        id = logId,
        userId = email,
        date = testDate,
        hasMenstruation = true,
        menstrualFlow = MenstrualFlowLevel.HEAVY,
        sexualActivity = mutableListOf("protected"),
        mood = mutableListOf("happy"),
        symptoms = mutableListOf("cramps"),
        vaginalDischarge = mutableListOf("clear"),
        physicalActivity = mutableListOf("yoga"),
        pillsTaken = mutableListOf("ibuprofen"),
        waterIntake = 2.0,
        weight = 55.5,
        notes = "Feeling good"
    )

    @BeforeEach
    fun setupMocks() {
        val user = sampleUser()
        val dto = sampleDTO()
        val log = sampleLog()

        Mockito.`when`(userService.getUserByEmail(email)).thenReturn(DTOMapper.userEntityToDTO(user))
        Mockito.`when`(dailyLogService.createLog(email, dto)).thenReturn(log)
        Mockito.`when`(dailyLogService.getAllLogs()).thenReturn(listOf(log))
        Mockito.`when`(dailyLogService.getLogsByUser(email)).thenReturn(listOf(log))
        Mockito.`when`(dailyLogService.getLogByUserAndDate(email, testDate)).thenReturn(log)
        Mockito.`when`(dailyLogService.getLogById(logId)).thenReturn(log)
        Mockito.`when`(dailyLogService.updateLog(logId, dto)).thenReturn(log)
    }

    @Test
    fun `createLog should return CREATED`() {
        mockMvc.perform(
            post("/daily-log/new/$email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleDTO()))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(logId))
    }

    @Test
    fun `getAllLogs should return OK with logs`() {
        mockMvc.perform(get("/daily-log"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(logId))
    }

    @Test
    fun `getLogsByUser should return OK with logs`() {
        mockMvc.perform(get("/daily-log/user/$email"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].userId").value(email))
    }

    @Test
    fun `getLogByDate should return OK with single log`() {
        mockMvc.perform(get("/daily-log/user/$email/date/$testDate"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.date").value(testDate))
    }

    @Test
    fun `getLogById should return single log`() {
        mockMvc.perform(get("/daily-log/$logId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(logId))
    }

    @Test
    fun `updateLog should return updated log`() {
        mockMvc.perform(
            put("/daily-log/user/$email/date/$testDate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleDTO()))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(logId))
            .andExpect(jsonPath("$.notes").value("Feeling good"))
    }

    @Test
    fun `deleteLog should return OK`() {
        mockMvc.perform(delete("/daily-log/$logId"))
            .andExpect(status().isOk)
    }
}
