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

/**
 * Controlador que gestiona las operaciones relacionadas con los registros diarios (DailyLog).
 */
@Controller
@RequestMapping("/daily-log")
class DailyLogController {

    @Autowired
    private lateinit var dailyLogService: DailyLogService

    @Autowired
    private lateinit var userService: UserService

    /**
     * Crea un nuevo registro diario para un usuario especificado.
     *
     * @param email Correo electrónico del usuario.
     * @param dto Objeto de transferencia con los datos del registro diario.
     * @return El registro creado y el estado HTTP 201 (CREATED).
     */
    @PostMapping("/new/{email}")
    fun createLog(@PathVariable email: String, @RequestBody dto: DailyLogDTO): ResponseEntity<DailyLog> {
        val user = userService.getUserByEmail(email)
        val log = dailyLogService.createLog(user.email, dto)
        return ResponseEntity(log, HttpStatus.CREATED)
    }

    /**
     * Obtiene todos los registros diarios del sistema.
     *
     * @return Lista de registros diarios y el estado HTTP 200 (OK).
     */
    @GetMapping
    fun getAllLogs(): ResponseEntity<List<DailyLog>> {
        val logs = dailyLogService.getAllLogs()
        return ResponseEntity(logs, HttpStatus.OK)
    }

    /**
     * Obtiene todos los registros diarios de un usuario específico.
     *
     * @param userId ID codificado (email) del usuario.
     * @return Lista de registros diarios del usuario y el estado HTTP 200 (OK).
     */
    @GetMapping("/user/{userId}")
    fun getLogsByUser(@PathVariable userId: String): ResponseEntity<List<DailyLog>> {
        val decodedEmail: String = URLDecoder.decode(userId, StandardCharsets.UTF_8)
        println(decodedEmail)
        val logs = dailyLogService.getLogsByUser(decodedEmail)
        return ResponseEntity(logs, HttpStatus.OK)
    }

    /**
     * Obtiene un registro diario de un usuario para una fecha específica.
     *
     * @param userId ID del usuario (correo electrónico).
     * @param date Fecha del registro en formato ISO (yyyy-MM-dd).
     * @return Registro diario encontrado y el estado HTTP 200 (OK).
     */
    @GetMapping("/user/{userId}/date/{date}")
    fun getLogByDate(
        @PathVariable userId: String,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: String
    ): ResponseEntity<DailyLog> {
        val log = dailyLogService.getLogByUserAndDate(userId, date)
        return ResponseEntity(log, HttpStatus.OK)
    }

    /**
     * Obtiene un registro diario por su ID.
     *
     * @param id ID del registro diario.
     * @return Registro diario encontrado y el estado HTTP 200 (OK).
     */
    @GetMapping("/{id}")
    fun getLogById(@PathVariable id: String): ResponseEntity<DailyLog> {
        val log = dailyLogService.getLogById(id)
        return ResponseEntity(log, HttpStatus.OK)
    }

    /**
     * Actualiza un registro diario existente para un usuario y una fecha determinados.
     *
     * @param userId ID del usuario (correo electrónico).
     * @param date Fecha del registro en formato ISO (yyyy-MM-dd).
     * @param dto Objeto de transferencia con los nuevos datos.
     * @return Registro actualizado y el estado HTTP 200 (OK).
     */
    @PutMapping("/user/{userId}/date/{date}")
    fun updateLog(
        @PathVariable userId: String,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: String,
        @RequestBody dto: DailyLogDTO
    ): ResponseEntity<DailyLog?> {
        val log = dailyLogService.getLogByUserAndDate(userId, date)
        val update = log.id?.let { dailyLogService.updateLog(it, dto) }
        return ResponseEntity(update, HttpStatus.OK)
    }

    /**
     * Elimina un registro diario por su ID.
     *
     * @param id ID del registro a eliminar.
     */
    @DeleteMapping("/{id}")
    fun deleteLog(@PathVariable id: String) {
        dailyLogService.deleteLog(id)
    }
}