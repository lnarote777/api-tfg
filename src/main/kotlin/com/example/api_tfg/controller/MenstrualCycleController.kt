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
    fun getPrediction(@PathVariable email: String): MenstrualCycle {
        return cycleService.predictNextCycle(email)
    }

    @PutMapping("/update")
    fun updateCycle(@RequestBody cycle: MenstrualCycle): ResponseEntity<MenstrualCycle> {
        val updated = cycleService.updateCycle(cycle)
        return ResponseEntity(updated, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteCycle(@PathVariable id: String): Boolean {
        return cycleService.deleteCycle(id)
    }
}