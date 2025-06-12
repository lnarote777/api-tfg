package com.example.api_tfg.controller

import com.example.api_tfg.dto.MenstrualCycleDTO
import com.example.api_tfg.model.MenstrualCycle
import com.example.api_tfg.service.MenstrualCycleService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

/**
 * Controlador que gestiona las operaciones relacionadas con los ciclos menstruales.
 */
@Controller
@RequestMapping("/cycles")
class MenstrualCycleController {
    @Autowired
    private lateinit var cycleService: MenstrualCycleService

    /**
     * Crea un nuevo ciclo menstrual con los datos proporcionados.
     *
     * @param cycleInsert DTO con la información para crear el ciclo menstrual.
     * @param httpRequest Petición HTTP (no utilizado actualmente).
     * @return El ciclo menstrual creado y el estado HTTP 201 (CREATED).
     */
    @PostMapping("/new")
    fun createCycle(@RequestBody cycleInsert: MenstrualCycleDTO,httpRequest: HttpServletRequest): ResponseEntity<MenstrualCycle> {
        val cycle = cycleService.createCycle(cycleInsert)
        return ResponseEntity(cycle, HttpStatus.CREATED)
    }

    /**
     * Obtiene todos los ciclos menstruales asociados a un usuario dado por su correo electrónico.
     *
     * @param email Correo electrónico del usuario.
     * @param httpRequest Petición HTTP (no utilizado actualmente).
     * @return Lista de ciclos menstruales y el estado HTTP 200 (OK).
     */
    @GetMapping("/user/{email}")
    fun getCyclesByUser(@PathVariable email: String, httpRequest: HttpServletRequest): ResponseEntity<List<MenstrualCycle>> {
        val cycles = cycleService.getCyclesByUserEmail(email)
        return ResponseEntity(cycles, HttpStatus.OK)
    }

    /**
     * Obtiene la predicción de los próximos ciclos menstruales para un usuario.
     *
     * @param email Correo electrónico del usuario.
     * @return Lista mutable de ciclos menstruales predichos (puede incluir nulos) y estado HTTP 200 (OK).
     */
    @GetMapping("/user/{email}/prediction")
    fun getPrediction(@PathVariable email: String): ResponseEntity<MutableList<MenstrualCycle?>> {
        val prediction = cycleService.predictNextCycle(email)
        return ResponseEntity(prediction, HttpStatus.OK)
    }

    /**
     * Actualiza un ciclo menstrual existente con la información proporcionada.
     *
     * @param cycle Objeto MenstrualCycle con los datos actualizados.
     * @return El ciclo menstrual actualizado y el estado HTTP 200 (OK).
     */
    @PutMapping("/update")
    fun updateCycle(@RequestBody cycle: MenstrualCycle): ResponseEntity<MenstrualCycle> {
        val updated = cycleService.updateCycle(cycle)
        return ResponseEntity(updated, HttpStatus.OK)
    }

    /**
     * Elimina un ciclo menstrual por su ID.
     *
     * @param id ID del ciclo menstrual a eliminar.
     * @return Booleano indicando si la eliminación fue exitosa, junto con estado HTTP 200 (OK).
     */
    @DeleteMapping("/{id}")
    fun deleteCycle(@PathVariable id: String): ResponseEntity<Boolean> {
        val deleted = cycleService.deleteCycle(id)
        return ResponseEntity.ok(deleted)
    }

    /**
     * Recalcula el ciclo menstrual de un usuario si no hay sangrado registrado para la fecha dada o la actual.
     *
     * @param userId ID del usuario (normalmente correo electrónico).
     * @param date Fecha opcional en formato ISO (yyyy-MM-dd). Si no se proporciona, se usa la fecha actual.
     * @return El ciclo menstrual recalculado o 404 (Not Found) si no existe.
     */
    @GetMapping("/recalculate/{userId}")
    fun recalculateCycle(
        @PathVariable userId: String,
        @RequestParam(required = false) date: String?
    ): ResponseEntity<MenstrualCycle> {
        val today = date?.let { LocalDate.parse(it) } ?: LocalDate.now()
        val cycle = cycleService.recalculateCycleIfNoBleeding(userId, today)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity(cycle, HttpStatus.OK)
    }
}