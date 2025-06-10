package com.example.api_tfg.pruebasUnitarias.services

import com.example.api_tfg.dto.MenstrualCycleDTO
import com.example.api_tfg.model.CyclePhase
import com.example.api_tfg.model.MenstrualCycle
import com.example.api_tfg.model.MenstrualFlowLevel
import com.example.api_tfg.repository.MenstrualCycleRepository
import com.example.api_tfg.service.MenstrualCycleService
import com.mongodb.assertions.Assertions.assertFalse
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MenstrualCycleServiceTest {

    private lateinit var menstrualCycleRepository: MenstrualCycleRepository
    private lateinit var service: MenstrualCycleService

    @BeforeEach
    fun setup() {
        menstrualCycleRepository = mockk(relaxed = true)
        service = MenstrualCycleService()
        service.apply {
            this::class.java.getDeclaredField("menstrualCycleRepository")
                .apply { isAccessible = true }
                .set(this, menstrualCycleRepository)
        }
    }

    @Test
    fun `createCycle should create and return a menstrual cycle`() {
        val dto = MenstrualCycleDTO(
            userId = "user@example.com",
            startDate = "2025-06-01",
            cycleLength = 28,
            bleedingDuration = 5,
            averageFlow = MenstrualFlowLevel.MODERATE,
            isPredicted = false
        )
        val captured = slot<MenstrualCycle>()
        every { menstrualCycleRepository.save(capture(captured)) } answers { captured.captured }

        val result = service.createCycle(dto)

        assertEquals(dto.userId, result.userId)
        assertEquals(dto.startDate, result.startDate)
        assertEquals(dto.cycleLength, result.cycleLength)
        assertEquals(dto.bleedingDuration, result.bleedingDuration)
        assertEquals(dto.averageFlow, result.averageFlow)
        assertFalse(result.isPredicted)
        assertTrue(result.phases.isNotEmpty())
    }

    @Test
    fun `getCyclesByUserEmail should return list of cycles`() {
        val userId = "user@example.com"
        val mockCycles = listOf(
            MenstrualCycle(
                userId = userId,
                startDate = "2025-06-01",
                endDate = "2025-06-28",
                cycleLength = 28,
                bleedingDuration = 5,
                averageFlow = MenstrualFlowLevel.MODERATE,
                isPredicted = false,
                logs = emptyList(),
                phases = emptyList()
            )
        )
        every { menstrualCycleRepository.findByUserId(userId) } returns mockCycles

        val result = service.getCyclesByUserEmail(userId)

        assertEquals(1, result.size)
        assertEquals("2025-06-01", result[0].startDate)
    }

    @Test
    fun `predictNextCycle should create 6 predicted cycles`() {
        val userId = "user@example.com"
        val lastCycle = MenstrualCycle(
            userId = userId,
            startDate = "2025-06-01",
            endDate = "2025-06-28",
            cycleLength = 28,
            bleedingDuration = 5,
            averageFlow = MenstrualFlowLevel.MODERATE,
            isPredicted = false,
            logs = emptyList(),
            phases = emptyList()
        )

        every { menstrualCycleRepository.findByUserId(userId) } returns listOf(lastCycle)
        every { menstrualCycleRepository.findTopByUserIdOrderByStartDateDesc(userId) } returns lastCycle
        every { menstrualCycleRepository.saveAll(any<List<MenstrualCycle>>()) }  answers { firstArg<List<MenstrualCycle>>() }

        val result = service.predictNextCycle(userId)

        assertEquals(6, result.size)
        assertTrue(result.all { it!!.isPredicted })
    }

    @Test
    fun `updateCycle should update and return cycle`() {
        val existing = MenstrualCycle(
            userId = "user@example.com",
            startDate = "2025-06-01",
            endDate = "2025-06-28",
            cycleLength = 28,
            bleedingDuration = 5,
            averageFlow = MenstrualFlowLevel.MODERATE,
            isPredicted = false,
            logs = mutableListOf(),
            phases = emptyList()
        )

        existing.id = "ciclo123"
        every { menstrualCycleRepository.findById("ciclo123") } returns java.util.Optional.of(existing)
        every { menstrualCycleRepository.save(any<MenstrualCycle>()) }  answers { firstArg<MenstrualCycle>() }

        val updated = existing.copy(cycleLength = 30)
        val result = service.updateCycle(updated)

        assertEquals(30, result.cycleLength)
        assertTrue(result.phases.isNotEmpty())
    }

    @Test
    fun `deleteCycle should return true if deleted`() {
        every { menstrualCycleRepository.existsById("id123") } returns true
        every { menstrualCycleRepository.deleteById("id123") } just Runs

        val result = service.deleteCycle("id123")

        assertTrue(result)
    }

    @Test
    fun `recalculateCycleIfNoBleeding should return a new predicted cycle`() {
        val userId = "user@example.com"
        val today = LocalDate.of(2025, 6, 10)
        val lastCycle = MenstrualCycle(
            userId = userId,
            startDate = "2025-05-10",
            endDate = "2025-06-06",
            cycleLength = 28,
            bleedingDuration = 5,
            averageFlow = MenstrualFlowLevel.MODERATE,
            isPredicted = false,
            logs = emptyList(),
            phases = emptyList()
        )
        every { menstrualCycleRepository.findByUserId(userId) } returns listOf(lastCycle)
        every { menstrualCycleRepository.save(any()) } answers { firstArg() }

        val result = service.recalculateCycleIfNoBleeding(userId, today)

        assertNotNull(result)
        assertTrue(result.isPredicted)
        assertEquals(today.plusDays(1).toString(), result.startDate)
    }

    @Test
    fun `deletePredictedCyclesForUser should delete predicted cycles`() {
        val userId = "user@example.com"
        val predictedCycle = MenstrualCycle(
            userId = userId,
            startDate = "2025-07-01",
            endDate = "2025-07-28",
            cycleLength = 28,
            bleedingDuration = 5,
            averageFlow = MenstrualFlowLevel.MODERATE,
            isPredicted = true,
            logs = emptyList(),
            phases = emptyList()
        )
        every { menstrualCycleRepository.findByUserId(userId) } returns listOf(predictedCycle)
        every { menstrualCycleRepository.deleteAll(any()) } just Runs

        service.deletePredictedCyclesForUser(userId)

        verify {
            menstrualCycleRepository.deleteAll(match {
                (it as List<*>).size == 1 && (it[0] as MenstrualCycle).isPredicted
            })
        }
    }
}
