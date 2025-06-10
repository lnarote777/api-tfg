package com.example.api_tfg.pruebasUnitarias.controllers

import com.example.api_tfg.controller.PayController
import com.example.api_tfg.model.Goal
import com.example.api_tfg.model.Subscription
import com.example.api_tfg.model.SubscriptionType
import com.example.api_tfg.model.UserEntity
import com.example.api_tfg.repository.UserRepository
import com.stripe.model.Event
import com.stripe.model.checkout.Session
import com.stripe.param.checkout.SessionCreateParams
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockedStatic
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import java.util.*


class PayControllerTest {

    private lateinit var userRepository: UserRepository
    private lateinit var controller: PayController

    private val stripeSecretKey = "sk_test_123"
    private val successUrl = "http://success.url"
    private val cancelUrl = "http://cancel.url"
    private val endpointSecret = "whsec_testsecret"

    private val monthlyPriceId = "price_monthly"
    private val oneTimePriceId = "price_one_time"

    @BeforeEach
    fun setup() {
        userRepository = mock(UserRepository::class.java)

        controller = PayController(
            stripeSecretKey,
            successUrl,
            cancelUrl,
            endpointSecret
        )
        // Inyectar valores privados
        val monthlyField = controller.javaClass.getDeclaredField("monthlyPriceId")
        monthlyField.isAccessible = true
        monthlyField.set(controller, monthlyPriceId)

        val oneTimeField = controller.javaClass.getDeclaredField("oneTimePriceId")
        oneTimeField.isAccessible = true
        oneTimeField.set(controller, oneTimePriceId)

        val userRepoField = controller.javaClass.getDeclaredField("userRepository")
        userRepoField.isAccessible = true
        userRepoField.set(controller, userRepository)
    }

    @Test
    fun `createSubscription returns session url for monthly subscription`() {
        // Mock estático de Session.create()
        val sessionMock = mock(Session::class.java)
        `when`(sessionMock.url).thenReturn("http://stripe.session.url")

        val mockStatic: MockedStatic<Session> = mockStatic(Session::class.java)
        mockStatic.`when`<Session> {
            Session.create(any(SessionCreateParams::class.java))
        }.thenReturn(sessionMock)

        val request = Subscription(
            email = "test@example.com",
            type = SubscriptionType.MONTHLY
        )

        val response = controller.createSubscription(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("http://stripe.session.url", response.body?.get("url"))

        mockStatic.close()
    }

    @Test
    fun `handleStripeWebhook processes checkout session completed event successfully`() {
        val fakePayload = "{}"
        val sigHeader = "stripe-signature"

        val httpRequest = mock(HttpServletRequest::class.java)
        `when`(httpRequest.getHeader("Stripe-Signature")).thenReturn(sigHeader)

        // Mock Session
        val sessionMock = mock(Session::class.java)
        `when`(sessionMock.customerEmail).thenReturn("test@example.com")

// Mock DataObjectDeserializer
        val deserializerMock = mock(com.stripe.model.EventDataObjectDeserializer::class.java)
        `when`(deserializerMock.getObject()).thenReturn(Optional.of(sessionMock))
// Mock Event
        val eventMock = mock(Event::class.java)
        `when`(eventMock.type).thenReturn("checkout.session.completed")
        `when`(eventMock.dataObjectDeserializer).thenReturn(deserializerMock)

        // Mock estático Webhook.constructEvent
        val webhookMockStatic: MockedStatic<com.stripe.net.Webhook> = mockStatic(com.stripe.net.Webhook::class.java)
        webhookMockStatic.`when`<Event> {
            com.stripe.net.Webhook.constructEvent(anyString(), anyString(), anyString())
        }.thenReturn(eventMock)

        // Mock UserRepository y usuario
        val user = UserEntity(
            _id = "test@example.com",
            name = "Test Name",
            username = "testuser",
            password = "password",
            roles = "USER",
            birthDate = "1990-01-01",
            registrationDate = Date(),
            weight = 70.0,
            goal = Goal.TRACK_PERIOD
        )
        `when`(userRepository.findUserBy_id("test@example.com")).thenReturn(Optional.of(user))
        `when`(userRepository.save(any(UserEntity::class.java))).thenReturn(user)

        val response = controller.handleStripeWebhook(fakePayload, httpRequest)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Evento procesado correctamente", response.body)
        assertEquals("PREMIUM", user.roles)

        webhookMockStatic.close()
    }

    @Test
    fun `handleStripeWebhook returns bad request if signature header missing`() {
        val httpRequest = mock(HttpServletRequest::class.java)
        `when`(httpRequest.getHeader("Stripe-Signature")).thenReturn(null)

        val response = controller.handleStripeWebhook("{}", httpRequest)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Falta la firma", response.body)
    }

    @Test
    fun `handleStripeWebhook returns bad request if webhook signature verification fails`() {
        val fakePayload = "{}"
        val sigHeader = "bad-signature"

        val httpRequest = mock(HttpServletRequest::class.java)
        `when`(httpRequest.getHeader("Stripe-Signature")).thenReturn(sigHeader)

        val webhookMockStatic: MockedStatic<com.stripe.net.Webhook> = mockStatic(com.stripe.net.Webhook::class.java)
        webhookMockStatic.`when`<Event> {
            com.stripe.net.Webhook.constructEvent(anyString(), anyString(), anyString())
        }.thenThrow(RuntimeException("invalid signature"))

        val response = controller.handleStripeWebhook(fakePayload, httpRequest)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertTrue(response.body?.contains("Webhook error") == true)

        webhookMockStatic.close()
    }
}