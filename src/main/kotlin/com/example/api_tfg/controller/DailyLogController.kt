package com.example.api_tfg.controller

import com.example.api_tfg.model.DailyLog
import com.example.api_tfg.service.DailyLogService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Controller
@RequestMapping("/daily-log")
class DailyLogController(
    private val dailyLogService: DailyLogService
) {

    @PostMapping()
    fun createLog(@RequestBody log: DailyLog): DailyLog {
        return dailyLogService.createLog(log)
    }

    @GetMapping("/user/{userId}")
    fun getLogsByUser(@PathVariable userId: String): List<DailyLog> {
        return dailyLogService.getLogsByUser(userId)
    }

    @GetMapping("/user/{userId}/date/{date}")
    fun getLogByDate(
        @PathVariable userId: String,
        @PathVariable date: String
    ): DailyLog? {
        val localDate = LocalDate.parse(date)
        return dailyLogService.getLogByUserAndDate(userId, localDate)
    }

    @PutMapping("/{id}")
    fun updateLog(@PathVariable id: String, @RequestBody log: DailyLog): DailyLog? {
        return dailyLogService.updateLog(id, log)
    }

    @DeleteMapping("/{id}")
    fun deleteLog(@PathVariable id: String) {
        dailyLogService.deleteLog(id)
    }
}