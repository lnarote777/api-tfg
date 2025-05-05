package com.example.api_tfg.service

import com.example.api_tfg.model.DailyLog
import com.example.api_tfg.repository.DailyLogRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DailyLogService {
    @Autowired
    private lateinit var dailyLogRepository: DailyLogRepository

    fun createLog(log: DailyLog): DailyLog {
        return dailyLogRepository.save(log)
    }

    fun getLogsByUser(userId: String): List<DailyLog> {
        return dailyLogRepository.findByUserId(userId)
    }

    fun getLogByUserAndDate(userId: String, date: java.time.LocalDate): DailyLog? {
        return dailyLogRepository.findByUserIdAndDate(userId, date)
    }

    fun updateLog(id: String, updatedLog: DailyLog): DailyLog? {
        val existingLog = dailyLogRepository.findById(id).orElse(null) ?: return null
        val logToSave = updatedLog.copy(id = existingLog.id)
        return dailyLogRepository.save(logToSave)
    }

    fun deleteLog(id: String) {
        dailyLogRepository.deleteById(id)
    }
}
