package com.example.api_tfg.service

import com.example.api_tfg.dto.MenstrualCycleDTO
import com.example.api_tfg.error.exception.NotFoundException
import com.example.api_tfg.model.CyclePhase
import com.example.api_tfg.model.CyclePhaseDay
import com.example.api_tfg.model.DailyLog
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
        val startDate = LocalDate.parse(cycleDTO.startDate)
        val endDate = startDate.plusDays(cycleDTO.cycleLength.toLong() - 1)
        val phases = generatePhasesForCycle(startDate, cycleDTO.cycleLength, cycleDTO.bleedingDuration)

        val cycle = MenstrualCycle(
            userId = cycleDTO.userId,
            startDate = startDate.toString(),
            endDate = endDate.toString(),
            cycleLength = cycleDTO.cycleLength,
            bleedingDuration = cycleDTO.bleedingDuration,
            averageFlow = cycleDTO.averageFlow,
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
        val lastStartDate = LocalDate.parse(lastCycle.startDate)
        val nextStartDate = lastStartDate.plusDays(lastCycle.cycleLength.toLong())
        val nextEndDate = nextStartDate.plusDays(lastCycle.bleedingDuration.toLong() - 1)
        val phases = generatePhasesForCycle(nextStartDate, lastCycle.cycleLength, lastCycle.bleedingDuration)

        val predictedCycle = MenstrualCycle(
            userId = email,
            startDate = nextStartDate.toString(),
            endDate = nextEndDate.toString(),
            cycleLength = lastCycle.cycleLength,
            bleedingDuration = lastCycle.bleedingDuration,
            averageFlow = lastCycle.averageFlow,
            isPredicted = true,
            phases = phases
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
            phases.add(CyclePhaseDay(date.toString(), phase))
        }

        return phases
    }

    fun updateCycle(cycle: MenstrualCycle): MenstrualCycle {
        val existing = cycle.id?.let {
            menstrualCycleRepository.findById(it)
                .orElseThrow { NotFoundException("Ciclo con id ${cycle.id} no encontrado.") }
        }

        val startDate = LocalDate.parse(cycle.startDate)
        val endDate = startDate.plusDays(cycle.cycleLength.toLong() - 1)
        val phases = generatePhasesForCycle(startDate, cycle.cycleLength, cycle.bleedingDuration)

        val updatedCycle = existing!!.copy(
            startDate = startDate.toString(),
            endDate = endDate.toString(),
            cycleLength = cycle.cycleLength,
            bleedingDuration = cycle.bleedingDuration,
            averageFlow = cycle.averageFlow,
            isPredicted = cycle.isPredicted,
            logs = cycle.logs,
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
    fun recalculateCycleIfNoBleeding(userId: String, today: LocalDate): MenstrualCycle? {
        val userCycles = menstrualCycleRepository.findByUserId(userId)
            .sortedByDescending { it.startDate }

        val currentCycle = userCycles.find {
            val start = LocalDate.parse(it.startDate)
            val end = LocalDate.parse(it.endDate)
            !it.isPredicted && today in start..end
        } ?: return null

        val hasBleeding = currentCycle.logs.any {
            LocalDate.parse(it.date) == today && it.hasMenstruation
        }

        if (hasBleeding) return currentCycle // No se recalcula si hay sangrado

        // Recalcular fechas
        val newStart = today.minusDays(currentCycle.cycleLength.toLong())
        val newEnd = newStart.plusDays(currentCycle.cycleLength.toLong() - 1)

        val newPhases = generatePhasesForCycle(
            newStart,
            currentCycle.cycleLength,
            currentCycle.bleedingDuration
        )

        val updatedCycle = currentCycle.copy(
            startDate = newStart.toString(),
            endDate = newEnd.toString(),
            phases = newPhases,
            isPredicted = true
        )

        menstrualCycleRepository.save(updatedCycle)
        return updatedCycle
    }
}