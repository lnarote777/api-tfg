package com.example.api_tfg.pruebasUnitarias.controllers

import com.example.api_tfg.controller.PaymentRedirectController
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import kotlin.test.assertEquals

@WebMvcTest(PaymentRedirectController::class)
@WithMockUser
class PaymentRedirectControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `paymentSuccess should redirect to custom URI with session_id`() {
        val sessionId = "abc123"

        val result = mockMvc.get("/payment-success") {
            param("session_id", sessionId)
        }.andReturn()

        val response = result.response
        assertEquals(HttpStatus.FOUND.value(), response.status)
        val locationHeader = response.getHeader("Location")
        assertEquals("com.example.interfaz_tfg://payment-success?session_id=$sessionId", locationHeader)
    }

    @Test
    fun `paymentCancel should redirect to cancel URI`() {
        val result = mockMvc.get("/payment-cancel").andReturn()

        val response = result.response
        assertEquals(HttpStatus.FOUND.value(), response.status)
        val locationHeader = response.getHeader("Location")
        assertEquals("com.example.interfaz_tfg://payment-cancel", locationHeader)
    }
}
