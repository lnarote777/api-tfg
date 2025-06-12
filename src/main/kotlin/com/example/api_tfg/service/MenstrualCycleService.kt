package com.example.api_tfg.service

import com.example.api_tfg.dto.MenstrualCycleDTO
import com.example.api_tfg.error.exception.NotFoundException
import com.example.api_tfg.model.CyclePhase
import com.example.api_tfg.model.CyclePhaseDay
import com.example.api_tfg.model.DailyLog
import com.example.api_tfg.model.MenstrualCycle
import com.example.api_tfg.repository.DailyLogRepository
import com.example.api_tfg.repository.MenstrualCycleRepository
import com.example.api_tfg.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MenstrualCycleService {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var menstrualCycleRepository: MenstrualCycleRepository
    @Autowired
    private lateinit var dailyLogRepository: DailyLogRepository

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
        userRepository.findUserBy_id(email).orElseThrow { NotFoundException("No se encontró nigun usuario con el email: $email") }
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
        val lastCycleStart = LocalDate.parse(lastCycle.startDate)
        val lastCycleEnd = LocalDate.parse(lastCycle.endDate)

        val todayLog = dailyLogRepository.findByUserIdAndDate(userId, today.toString()).orElse(null)
        val yesterdayLog = dailyLogRepository.findByUserIdAndDate(userId, today.minusDays(1).toString()).orElse(null)

        val hasBleedingToday = todayLog?.hasMenstruation == true
        val hadBleedingYesterday = yesterdayLog?.hasMenstruation == true

        val isInCurrentCycle = today in lastCycleStart..lastCycleEnd

        // Si hay sangrado hoy, no tocar ciclo real ni predicción, sólo retornar el ciclo actual
        if (hasBleedingToday && isInCurrentCycle && !lastCycle.isPredicted) {
            return lastCycle
        }

        // Si no hay sangrado hoy pero sí ayer y estamos dentro del ciclo real, ajustamos sólo fase menstruación
        if (!hasBleedingToday && hadBleedingYesterday && isInCurrentCycle && !lastCycle.isPredicted) {
            val updatedPhases = lastCycle.phases.map {
                if (it.phase == CyclePhase.MENSTRUATION && LocalDate.parse(it.date) >= today) {
                    // terminamos menstruación el día anterior a hoy
                    it.copy(phase = CyclePhase.FOLLICULAR) // o una fase neutra para el resto
                } else it
            }

            val updatedCycle = lastCycle.copy(phases = updatedPhases)
            return menstrualCycleRepository.save(updatedCycle)
        }

        // Si estamos dentro de un ciclo real sin predicción y no aplican casos anteriores, no recalcular nada
        if (isInCurrentCycle && !lastCycle.isPredicted) {
            return lastCycle
        }

        // Si no hay sangrado reciente, eliminar predicciones y crear nuevo ciclo predicho
        deletePredictedCyclesForUser(userId)

        val newStart = if (hadBleedingYesterday || hasBleedingToday) today else today.plusDays(1)
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
            logs = emptyList()
        )

        return menstrualCycleRepository.save(newCycle)
    }

    fun deletePredictedCyclesForUser(userId: String) {
        val predicted = menstrualCycleRepository.findByUserId(userId)
            .filter { it.isPredicted }
        menstrualCycleRepository.deleteAll(predicted)
    }

}