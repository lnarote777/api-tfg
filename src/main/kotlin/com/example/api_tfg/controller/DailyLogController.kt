package com.example.api_tfg.controller

import com.example.api_tfg.dto.DailyLogDTO
import com.example.api_tfg.model.DailyLog
import com.example.api_tfg.service.DailyLogService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Controller
@RequestMapping("/daily-log")
class DailyLogController(
    private val dailyLogService: DailyLogService
) {

    @PostMapping("/{userId}")
    fun createLog(@PathVariable userId: String, @RequestBody dto: DailyLogDTO): DailyLog {
        return dailyLogService.createLog(userId, dto)
    }

    @GetMapping
    fun getAllLogs(): List<DailyLog> {
        return dailyLogService.getAllLogs()
    }

    @GetMapping("/user/{userId}")
    fun getLogsByUser(@PathVariable userId: String): List<DailyLog> {
        return dailyLogService.getLogsByUser(userId)
    }

    @GetMapping("/user/{userId}/date/{date}")
    fun getLogByDate(
        @PathVariable userId: String,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): DailyLog? {
        return dailyLogService.getLogByUserAndDate(userId, date)
    }

    @GetMapping("/{id}")
    fun getLogById(@PathVariable id: String): DailyLog {
        return dailyLogService.getLogById(id)
    }

    @PutMapping("/{id}")
    fun updateLog(@PathVariable id: String, @RequestBody log: DailyLogDTO): DailyLog? {
        return dailyLogService.updateLog(id, log)
    }

    @DeleteMapping("/{id}")
    fun deleteLog(@PathVariable id: String) {
        dailyLogService.deleteLog(id)
    }
}