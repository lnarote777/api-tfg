package com.example.api_tfg.repository

import com.example.api_tfg.model.DailyLog
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.Optional

@Repository
interface DailyLogRepository: MongoRepository<DailyLog, String> {
    fun findByUserId(userId: String): List<DailyLog>
    fun findByUserIdAndDate(userId: String, date: String): Optional<DailyLog>
}