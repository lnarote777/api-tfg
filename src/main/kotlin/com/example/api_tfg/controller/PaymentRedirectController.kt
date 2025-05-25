package com.example.api_tfg.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
class PaymentRedirectController {

    @GetMapping("/payment-success")
    fun paymentSuccess(@RequestParam("session_id") sessionId: String): ResponseEntity<Void> {
        val redirectUrl = UriComponentsBuilder.newInstance()
            .scheme("com.example.interfaz_tfg")
            .host("payment-success")
            .queryParam("session_id", sessionId)
            .build()
            .toUriString()

        val headers = HttpHeaders()
        headers.add("Location", redirectUrl)

        return ResponseEntity.status(302).headers(headers).build()
    }

    @GetMapping("/payment-cancel")
    fun paymentCancel(): ResponseEntity<Void> {
        val redirectUrl = "com.example.interfaz_tfg://payment-cancel"

        val headers = HttpHeaders()
        headers.add("Location", redirectUrl)

        return ResponseEntity.status(302).headers(headers).build()
    }
}
