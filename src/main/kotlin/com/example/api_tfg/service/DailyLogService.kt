package com.example.api_tfg.service

import com.example.api_tfg.dto.DailyLogDTO
import com.example.api_tfg.error.exception.NotFoundException
import com.example.api_tfg.model.DailyLog
import com.example.api_tfg.repository.DailyLogRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class DailyLogService {

    @Autowired
    private lateinit var dailyLogRepository: DailyLogRepository

    @Autowired
    private lateinit var menstrualCycleService: MenstrualCycleService

    fun createLog(userId: String, dto: DailyLogDTO): DailyLog {
        val existing = dailyLogRepository.findByUserIdAndDate(userId, dto.date)
        if (existing.isPresent  ) throw IllegalArgumentException("Ya hay un log para ese día.")

        val log = DailyLog(
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

        val savedLog = dailyLogRepository.save(log)

        // Recalcular solo si no hay sangrado
        if (!dto.hasMenstruation) {
            val parsedDate = LocalDate.parse(dto.date)
            menstrualCycleService.recalculateCycleIfNoBleeding(userId, parsedDate)
        }

        return savedLog
    }

    fun getAllLogs(): List<DailyLog> {
        return dailyLogRepository.findAll()
    }

    fun getLogsByUser(userId: String): List<DailyLog> {
        return dailyLogRepository.findByUserId(userId)
    }

    fun getLogByUserAndDate(userId: String, date: String): DailyLog {
        return dailyLogRepository.findByUserIdAndDate(userId, date).orElseThrow { NotFoundException("No se encontró log p ara el usuario $userId en $date") }
    }

    fun getLogById(id: String): DailyLog {
        return dailyLogRepository.findById(id)
            .orElseThrow { NotFoundException("Log con id $id no encontrado") }
    }


    fun updateLog(id: String, dto: DailyLogDTO): DailyLog? {
        val existing = dailyLogRepository.findById(id)

        if (existing.isPresent) {
            val existingLog = existing.get()
            val updated = existingLog.copy(
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
            return dailyLogRepository.save(updated)
        }else{
            return null
        }


    }

    fun deleteLog(id: String) {
        if (!dailyLogRepository.existsById(id)) {
            throw NotFoundException("Log con id $id no encontrado")
        }
        dailyLogRepository.deleteById(id)
    }

}
