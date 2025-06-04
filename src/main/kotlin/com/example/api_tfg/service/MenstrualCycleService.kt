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

    fun predictNextCycle(email: String): MutableList<MenstrualCycle?> {
        deletePredictedCyclesForUser(email)
        val lastCycle = menstrualCycleRepository.findTopByUserIdOrderByStartDateDesc(email)
            ?: throw NotFoundException("No hay ciclos previos para el usuario con $email")

        val predictedCycles = mutableListOf<MenstrualCycle>()

        var baseStartDate = LocalDate.parse(lastCycle.startDate)

        for (i in 1..6) {
            val nextStartDate = baseStartDate.plusDays(lastCycle.cycleLength.toLong())
            val nextEndDate = nextStartDate.plusDays(lastCycle.cycleLength.toLong() - 1)

            val phases = generatePhasesForCycle(
                nextStartDate,
                lastCycle.cycleLength,
                lastCycle.bleedingDuration
            )

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

            predictedCycles.add(predictedCycle)
            baseStartDate = nextStartDate
        }

        return menstrualCycleRepository.saveAll(predictedCycles)
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

        val lastCycle = userCycles.firstOrNull() ?: return null

        // Verificar si hoy o ayer tienen sangrado real
        val hasRecentBleeding = lastCycle.logs.any {
            val logDate = LocalDate.parse(it.date)
            (logDate == today || logDate == today.minusDays(1)) && it.hasMenstruation
        }

        val todayInCurrentCycle = today.isAfter(LocalDate.parse(lastCycle.startDate).minusDays(1)) &&
                today.isBefore(LocalDate.parse(lastCycle.endDate).plusDays(1))

        if (todayInCurrentCycle && !lastCycle.isPredicted) {
            return null // Ya hay un ciclo real que abarca hoy, no recalcular
        }

        deletePredictedCyclesForUser(userId)

        // Si hay sangrado hoy o ayer, empieza hoy, si no, empieza mañana
        val newStart = if (hasRecentBleeding) today else today.plusDays(1)
        val newEnd = newStart.plusDays(lastCycle.cycleLength.toLong() - 1)

        val newPhases = generatePhasesForCycle(
            newStart,
            lastCycle.cycleLength,
            lastCycle.bleedingDuration
        )

        val newCycle = lastCycle.copy(
            id = null,
            startDate = newStart.toString(),
            endDate = newEnd.toString(),
            phases = newPhases,
            isPredicted = true,
            logs = emptyList() // Vacío si es predicción
        )

        return menstrualCycleRepository.save(newCycle)
    }

    fun deletePredictedCyclesForUser(userId: String) {
        val predicted = menstrualCycleRepository.findByUserId(userId)
            .filter { it.isPredicted }
        menstrualCycleRepository.deleteAll(predicted)
    }

}