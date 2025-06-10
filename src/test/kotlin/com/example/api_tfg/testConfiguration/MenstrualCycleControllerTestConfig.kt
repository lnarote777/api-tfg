package com.example.api_tfg.testConfiguration

import com.example.api_tfg.service.MenstrualCycleService
import org.mockito.Mockito
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class MenstrualCycleControllerTestConfig {
    @Bean
    fun cycleService(): MenstrualCycleService = Mockito.mock(MenstrualCycleService::class.java)
}