package com.example.api_tfg.service

import com.example.api_tfg.dto.MenstrualCycleDTO
import com.example.api_tfg.error.exception.NotFoundException
import com.example.api_tfg.model.CyclePhase
import com.example.api_tfg.model.CyclePhaseDay
import com.example.api_tfg.model.MenstrualCycle
import com.example.api_tfg.repository.MenstrualCycleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MenstrualCycleService {
    @Autowired
    private lateinit var menstrualCycleRepository: MenstrualCycleRepository

    fun createCycle(cycleDTO: MenstrualCycleDTO): MenstrualCycle {
        val phases = generatePhasesForCycle(cycleDTO.startDate, cycleDTO.cycleLength, cycleDTO.bleedingDuration)
        val endDate = cycleDTO.startDate.plusDays(cycleDTO.cycleLength.toLong() - 1)

        val cycle = MenstrualCycle(
            userId = cycleDTO.userId,
            startDate = cycleDTO.startDate,
            endDate = endDate,
            cycleLength = cycleDTO.cycleLength,
            bleedingDuration = cycleDTO.bleedingDuration,
            averageFlow = cycleDTO.averageFlow,
            symptoms = cycleDTO.symptoms,
            moodChanges = cycleDTO.moodChanges,
            notes = cycleDTO.notes,
            isPredicted = cycleDTO.isPredicted,
            phases = phases
        )

        return menstrualCycleRepository.save(cycle)
    }

    fun getCyclesByUserEmail(email: String): List<MenstrualCycle> {
        return menstrualCycleRepository.findByUserId(email)
    }

    fun predictNextCycle(email: String): MenstrualCycle {
        val lastCycle = menstrualCycleRepository.findTopByUserIdOrderByStartDateDesc(email)
            ?: throw NotFoundException("No hay ciclos previos para el usuario con $email")


        val nextStartDate = lastCycle.startDate.plusDays(lastCycle.cycleLength.toLong())
        val nextEndDate = nextStartDate.plusDays(lastCycle.bleedingDuration.toLong() - 1)

        val predictedCycle = MenstrualCycle(
            userId = email,
            startDate = nextStartDate,
            endDate = nextEndDate,
            cycleLength = lastCycle.cycleLength,
            bleedingDuration = lastCycle.bleedingDuration,
            averageFlow = lastCycle.averageFlow,
            symptoms = emptyList(),
            moodChanges = emptyList(),
            notes = null,
            isPredicted = true
        )

        return menstrualCycleRepository.save(predictedCycle)
    }

    fun generatePhasesForCycle(startDate: LocalDate, cycleLength: Int, bleedingDuration: Int): List<CyclePhaseDay> {
        val phases = mutableListOf<CyclePhaseDay>()

        for (i in 0 until cycleLength) {
            val date = startDate.plusDays(i.toLong())
            val phase = when {
                i < bleedingDuration -> CyclePhase.MENSTRUATION
                i < 14 -> CyclePhase.FOLLICULAR
                i == 14 -> CyclePhase.OVULATION
                else -> CyclePhase.LUTEAL
            }
            phases.add(CyclePhaseDay(date, phase))
        }

        return phases
    }

    fun updateCycle(cycle: MenstrualCycle): MenstrualCycle {
        val existing = cycle.id?.let {
            menstrualCycleRepository.findById(it)
                .orElseThrow { NotFoundException("Ciclo con id ${cycle.id} no encontrado.") }
        }

        val endDate = cycle.startDate.plusDays(cycle.cycleLength.toLong() - 1)
        val phases = generatePhasesForCycle(cycle.startDate, cycle.cycleLength, cycle.bleedingDuration)

        val updatedCycle = existing!!.copy(
            startDate = cycle.startDate,
            endDate = endDate,
            cycleLength = cycle.cycleLength,
            bleedingDuration = cycle.bleedingDuration,
            averageFlow = cycle.averageFlow,
            symptoms = cycle.symptoms,
            moodChanges = cycle.moodChanges,
            notes = cycle.notes,
            isPredicted = cycle.isPredicted,
            phases = phases
        )

        return menstrualCycleRepository.save(updatedCycle)
    }

    fun deleteCycle(id: String): Boolean {
        try {
            if (!menstrualCycleRepository.existsById(id)) {
                throw NotFoundException("Ciclo con id $id no encontrado.")
            }
            menstrualCycleRepository.deleteById(id)
            return true
        }catch (ex: Exception){
            return false
        }

    }
}