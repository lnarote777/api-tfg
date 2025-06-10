package com.example.api_tfg.pruebasUnitarias.services

import com.example.api_tfg.dto.DailyLogDTO
import com.example.api_tfg.error.exception.NotFoundException
import com.example.api_tfg.model.DailyLog
import com.example.api_tfg.model.MenstrualFlowLevel
import com.example.api_tfg.repository.DailyLogRepository
import com.example.api_tfg.service.DailyLogService
import com.example.api_tfg.service.MenstrualCycleService
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*
import kotlinx.coroutines.test.runTest

class DailyLogServiceTest {

    private lateinit var dailyLogService: DailyLogService
    private val dailyLogRepository: DailyLogRepository = mockk()
    private val menstrualCycleService: MenstrualCycleService = mockk()

    @BeforeEach
    fun setUp() {
        dailyLogService = DailyLogService(dailyLogRepository, menstrualCycleService)
    }

    @Test
    fun `createLog should save log and recalculate cycle if no menstruation`() = runTest {
        val userId = "user1"
        val dto = DailyLogDTO(
            date = "2025-06-10",
            hasMenstruation = false,
            menstrualFlow = MenstrualFlowLevel.LIGHT,
            sexualActivity = mutableListOf("sí"),
            mood = mutableListOf("feliz"),
            symptoms = mutableListOf("dolor de cabeza"),
            vaginalDischarge = mutableListOf("transparente"),
            physicalActivity = mutableListOf("yoga"),
            pillsTaken = mutableListOf("ibuprofeno"),
            waterIntake = 1.5,
            weight = 60.0,
            notes = "Todo bien"
        )

        val savedLog = DailyLog(
            userId = userId,
            date = dto.date,
            hasMenstruation = dto.hasMenstruation,
            menstrualFlow = dto.menstrualFlow,
            sexualActivity = dto.sexualActivity,
            mood = dto.mood,
            symptoms = dto.symptoms,
            vaginalDischarge = dto.vaginalDischarge,
            physicalActivity = dto.physicalActivity,
            pillsTaken = dto.pillsTaken,
            waterIntake = dto.waterIntake,
            weight = dto.weight,
            notes = dto.notes
        )
        val savedSlot = slot<DailyLog>()

        // Mock repository responses
        every { dailyLogRepository.findByUserIdAndDate(userId, dto.date) } returns Optional.empty()
        every { dailyLogRepository.save(capture(savedSlot)) } returns savedLog

        // Mock coroutine service
        every {
            menstrualCycleService.recalculateCycleIfNoBleeding(userId, LocalDate.parse(dto.date))
        } returns null
        // When
        val result = dailyLogService.createLog(userId, dto)

        // Then
        assertEquals(userId, result.userId)
        assertEquals(dto.date, result.date)
        assertEquals(dto.sexualActivity, result.sexualActivity)
        assertEquals(dto.notes, result.notes)

        // Verify interactions
        verify(exactly = 1) {
            menstrualCycleService.recalculateCycleIfNoBleeding(userId, LocalDate.parse(dto.date))
        }
        verify(exactly = 1) { dailyLogRepository.save(any()) }
    }

    @Test
    fun `createLog throws exception if log already exists`() {
        val userId = "user1"
        val dto = DailyLogDTO(date = "2025-06-10")
        every { dailyLogRepository.findByUserIdAndDate(userId, dto.date) } returns Optional.of(DailyLog(userId = userId, date = dto.date))

        assertThrows(IllegalArgumentException::class.java) {
            dailyLogService.createLog(userId, dto)
        }
        verify(exactly = 0) { dailyLogRepository.save(any()) }
    }

    @Test
    fun `getLogByUserAndDate returns log if exists`() {
        val userId = "user1"
        val date = "2025-06-10"
        val log = DailyLog(id = "123", userId = userId, date = date)
        every { dailyLogRepository.findByUserIdAndDate(userId, date) } returns Optional.of(log)

        val result = dailyLogService.getLogByUserAndDate(userId, date)

        assertEquals(log, result)
    }

    @Test
    fun `getLogByUserAndDate throws NotFoundException if not found`() {
        every { dailyLogRepository.findByUserIdAndDate("user1", "2025-06-10") } returns Optional.empty()

        assertThrows(NotFoundException::class.java) {
            dailyLogService.getLogByUserAndDate("user1", "2025-06-10")
        }
    }

    @Test
    fun `updateLog updates log if exists`() {
        val id = "log123"
        val existing = DailyLog(id = id, userId = "user1", date = "2025-06-10")
        val dto = DailyLogDTO(date = "2025-06-11", hasMenstruation = true)
        val updated = existing.copy(date = dto.date, hasMenstruation = dto.hasMenstruation)

        every { dailyLogRepository.findById(id) } returns Optional.of(existing)
        every { dailyLogRepository.save(any()) } returns updated

        val result = dailyLogService.updateLog(id, dto)

        assertEquals("2025-06-11", result?.date)
        assertTrue(result?.hasMenstruation == true)
    }

    @Test
    fun `updateLog returns null if log not found`() {
        every { dailyLogRepository.findById("noId") } returns Optional.empty()
        val result = dailyLogService.updateLog("noId", DailyLogDTO(date = "2025-06-10"))
        assertNull(result)
    }

    @Test
    fun `deleteLog deletes if exists`() {
        every { dailyLogRepository.existsById("id123") } returns true
        every { dailyLogRepository.deleteById("id123") } just Runs

        dailyLogService.deleteLog("id123")

        verify { dailyLogRepository.deleteById("id123") }
    }

    @Test
    fun `deleteLog throws NotFoundException if not exists`() {
        every { dailyLogRepository.existsById("missing") } returns false

        assertThrows(NotFoundException::class.java) {
            dailyLogService.deleteLog("missing")
        }
    }
}