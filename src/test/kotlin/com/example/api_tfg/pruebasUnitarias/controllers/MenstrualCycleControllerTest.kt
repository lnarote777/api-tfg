package com.example.api_tfg.pruebasUnitarias.controllers

import com.example.api_tfg.dto.MenstrualCycleDTO
import com.example.api_tfg.model.*
import com.example.api_tfg.service.MenstrualCycleService
import com.example.api_tfg.testConfiguration.MenstrualCycleControllerTestConfig
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
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@Import(MenstrualCycleControllerTestConfig::class)
class MenstrualCycleControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var cycleService: MenstrualCycleService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val userId = "test@example.com"
    private val cycleId = "cycle123"
    private val startDate = "2025-06-01"
    private val endDate = "2025-06-28"

    private fun sampleDTO() = MenstrualCycleDTO(
        userId = userId,
        startDate = startDate,
        cycleLength = 28,
        bleedingDuration = 5,
        averageFlow = MenstrualFlowLevel.MODERATE,
        isPredicted = false
    )

    private fun sampleCycle() = MenstrualCycle(
        id = cycleId,
        userId = userId,
        startDate = startDate,
        endDate = endDate,
        cycleLength = 28,
        bleedingDuration = 5,
        averageFlow = MenstrualFlowLevel.MODERATE,
        logs = listOf(),
        isPredicted = false,
        phases = listOf()
    )

    @Suppress("UNCHECKED_CAST")
    fun <T> anyNonNull(): T = Mockito.any<T>() ?: null as T

    @BeforeEach
    fun setupMocks() {
        val cycle = sampleCycle()

        Mockito.`when`(cycleService.createCycle(anyNonNull())).thenReturn(cycle)
        Mockito.`when`(cycleService.updateCycle(anyNonNull())).thenReturn(cycle)
        Mockito.`when`(cycleService.getCyclesByUserEmail(userId)).thenReturn(listOf(cycle))
        Mockito.`when`(cycleService.predictNextCycle(userId)).thenReturn(mutableListOf(cycle))
        Mockito.`when`(cycleService.deleteCycle(cycleId)).thenReturn(true)
        Mockito.`when`(cycleService.recalculateCycleIfNoBleeding(userId, LocalDate.of(2025, 6, 10)))
            .thenReturn(cycle)
    }

    @Test
    fun `createCycle should return CREATED`() {
        mockMvc.perform(
            post("/cycles/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleDTO()))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(cycleId))
            .andExpect(jsonPath("$.userId").value(userId))
            .andExpect(jsonPath("$.cycleLength").value(28))
    }

    @Test
    fun `getCyclesByUser should return OK`() {
        mockMvc.perform(get("/cycles/user/$userId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].userId").value(userId))
    }

    @Test
    fun `getPrediction should return OK`() {
        mockMvc.perform(get("/cycles/user/$userId/prediction"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].userId").value(userId))
            .andExpect(jsonPath("$[0].isPredicted").value(false))
    }

    @Test
    fun `updateCycle should return OK with updated cycle`() {
        val updatedCycle = sampleCycle()
        mockMvc.perform(
            put("/cycles/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCycle))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(cycleId))
            .andExpect(jsonPath("$.bleedingDuration").value(5))
    }

    @Test
    fun `deleteCycle should return true`() {
        mockMvc.perform(delete("/cycles/$cycleId"))
            .andExpect(status().isOk)
            .andExpect(content().string("true"))
    }

    @Test
    fun `recalculateCycle should return OK`() {
        mockMvc.perform(get("/cycles/recalculate/$userId?date=2025-06-10"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(cycleId))
    }

    @Test
    fun `recalculateCycle should return 404 if not found`() {
        val otherId = "notfound@example.com"
        Mockito.`when`(cycleService.recalculateCycleIfNoBleeding(otherId, LocalDate.of(2025, 6, 10)))
            .thenReturn(null)

        mockMvc.perform(get("/cycles/recalculate/$otherId?date=2025-06-10"))
            .andExpect(status().isNotFound)
    }
}
