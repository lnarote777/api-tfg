package com.example.api_tfg.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

/**
 * Controlador encargado de redirigir las URLs de éxito y cancelación de pagos
 * hacia el esquema personalizado de la app cliente (interfaz móvil).
 *
 * Las URLs configuradas en Stripe para éxito y cancelación apuntan a estos endpoints,
 * que a su vez redirigen a las URLs internas de la app móvil para manejar la respuesta.
 */
@RestController
class PaymentRedirectController {

    /**
     * Endpoint al que Stripe redirige tras un pago exitoso.
     * Construye una URL personalizada para la app móvil con el ID de sesión de Stripe.
     *
     * @param sessionId ID de la sesión de pago proporcionado por Stripe.
     * @return Respuesta HTTP con redirección (302) a la URL interna de la app con el session_id.
     */
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

    /**
     * Endpoint al que Stripe redirige tras cancelar el pago.
     * Redirige a una URL interna de la app móvil para manejar la cancelación.
     *
     * @return Respuesta HTTP con redirección (302) a la URL interna de cancelación de la app.
     */
    @GetMapping("/payment-cancel")
    fun paymentCancel(): ResponseEntity<Void> {
        val redirectUrl = "com.example.interfaz_tfg://payment-cancel"

        val headers = HttpHeaders()
        headers.add("Location", redirectUrl)

        return ResponseEntity.status(302).headers(headers).build()
    }
}
