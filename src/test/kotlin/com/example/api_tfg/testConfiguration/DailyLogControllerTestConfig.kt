package com.example.api_tfg.testConfiguration

import com.example.api_tfg.service.DailyLogService
import com.example.api_tfg.service.UserService
import org.mockito.Mockito
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class DailyLogControllerTestConfig {

    @Bean
    fun dailyLogService(): DailyLogService = Mockito.mock(DailyLogService::class.java)

    @Bean
    fun userService(): UserService = Mockito.mock(UserService::class.java)
}