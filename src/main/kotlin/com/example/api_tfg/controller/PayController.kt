package com.example.api_tfg.controller

import com.example.api_tfg.model.Subscription
import com.example.api_tfg.model.SubscriptionType
import com.stripe.Stripe
import com.stripe.model.checkout.Session
import com.stripe.param.checkout.SessionCreateParams
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/pay")
class PayController(
    @Value("\${STRIPE_SECRET_KEY}")
    private val stripeSecretKey: String,
    @Value("\${frontend.success.url}")
    private val successUrl: String,
    @Value("\${frontend.cancel.url}")
    private val cancelUrl: String
) {

    @Value("\${stripe.price.monthly}")
    private lateinit var monthlyPriceId: String

    @Value("\${stripe.price.one_time}")
    private lateinit var oneTimePriceId: String


    init {
        Stripe.apiKey = stripeSecretKey
    }
    @PostMapping("/create-subscription")
    fun createSubscription(@RequestBody request: Subscription): ResponseEntity<Map<String, String>>{
        println("Success URL: $successUrl")
        println("Cancel URL: $cancelUrl")
        val priceId = when (request.type) {
            SubscriptionType.MONTHLY -> monthlyPriceId // ID de Stripe para suscripción mensual
            SubscriptionType.ONE_TIME -> oneTimePriceId // ID de Stripe para pago único
        }

        val mode = when (request.type) {
            SubscriptionType.MONTHLY -> SessionCreateParams.Mode.SUBSCRIPTION
            SubscriptionType.ONE_TIME -> SessionCreateParams.Mode.PAYMENT
        }

        val params = SessionCreateParams.builder()
            .setMode(mode)
            .setCustomerEmail(request.email)
            .setSuccessUrl(successUrl)
            .setCancelUrl(cancelUrl)
            .addLineItem(
                SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPrice(priceId)
                    .build()
            )
            .build()

        val session = Session.create(params)
        return ResponseEntity.ok(mapOf("url" to session.url))
    }
}

