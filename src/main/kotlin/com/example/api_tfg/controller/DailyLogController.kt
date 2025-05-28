package com.example.api_tfg.controller

import com.example.api_tfg.dto.DailyLogDTO
import com.example.api_tfg.model.DailyLog
import com.example.api_tfg.service.DailyLogService
import com.example.api_tfg.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


@Controller
@RequestMapping("/daily-log")
class DailyLogController {

    @Autowired
    private lateinit var dailyLogService: DailyLogService

    @Autowired
    private lateinit var userService: UserService

    @PostMapping("/new/{email}")
    fun createLog(@PathVariable email: String, @RequestBody dto: DailyLogDTO): ResponseEntity<DailyLog> {
        val user = userService.getUserByEmail(email)
        val log = dailyLogService.createLog(user.email, dto)
        return ResponseEntity(log, HttpStatus.CREATED)
    }

    @GetMapping
    fun getAllLogs(): List<DailyLog> {
        return dailyLogService.getAllLogs()
    }

    @GetMapping("/user/{userId}")
    fun getLogsByUser(@PathVariable userId: String): ResponseEntity<List<DailyLog>> {
        val decodedEmail: String = URLDecoder.decode(userId, StandardCharsets.UTF_8)
        println(decodedEmail)
        val logs = dailyLogService.getLogsByUser(decodedEmail)
        return ResponseEntity(logs, HttpStatus.OK)
    }

    @GetMapping("/user/{userId}/date/{date}")
    fun getLogByDate(
        @PathVariable userId: String,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: String
    ): DailyLog? {
        return dailyLogService.getLogByUserAndDate(userId, date)
    }

    @GetMapping("/{id}")
    fun getLogById(@PathVariable id: String): DailyLog {
        return dailyLogService.getLogById(id)
    }

    @PutMapping("/user/{userId}/date/{date}")
    fun updateLog(
        @PathVariable userId: String,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: String,
        @RequestBody dto: DailyLogDTO
    ): DailyLog? {
        val log = dailyLogService.getLogByUserAndDate(userId, date)
        return log.id?.let { dailyLogService.updateLog(it, dto) }
    }

    @DeleteMapping("/{id}")
    fun deleteLog(@PathVariable id: String) {
        dailyLogService.deleteLog(id)
    }
}