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

@Controller
@RequestMapping("/cycles")
class MenstrualCycleController {
    @Autowired
    private lateinit var cycleService: MenstrualCycleService

    @PostMapping("/new")
    fun createCycle(@RequestBody cycleInsert: MenstrualCycleDTO,httpRequest: HttpServletRequest): ResponseEntity<MenstrualCycle> {
        val cycle = cycleService.createCycle(cycleInsert)
        return ResponseEntity(cycle, HttpStatus.CREATED)
    }

    @GetMapping("/user/{email}")
    fun getCyclesByUser(@PathVariable email: String, httpRequest: HttpServletRequest): ResponseEntity<List<MenstrualCycle>> {
        val cycles = cycleService.getCyclesByUserEmail(email)
        return ResponseEntity(cycles, HttpStatus.OK)
    }


    @GetMapping("/user/{email}/prediction")
    fun getPrediction(@PathVariable email: String): ResponseEntity<MutableList<MenstrualCycle?>> {
        val prediction = cycleService.predictNextCycle(email)
        return ResponseEntity(prediction, HttpStatus.OK)
    }

    @PutMapping("/update")
    fun updateCycle(@RequestBody cycle: MenstrualCycle): ResponseEntity<MenstrualCycle> {
        val updated = cycleService.updateCycle(cycle)
        return ResponseEntity(updated, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteCycle(@PathVariable id: String): ResponseEntity<Boolean> {
        val deleted = cycleService.deleteCycle(id)
        return ResponseEntity.ok(deleted)
    }

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