package com.example.api_tfg

import com.example.api_tfg.security.RSAKeysProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(RSAKeysProperties::class)
class ApiTfgApplication

fun main(args: Array<String>) {
	runApplication<ApiTfgApplication>(*args)
}
