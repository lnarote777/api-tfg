package com.example.api_tfg.controller

import com.example.api_tfg.error.exception.NotFoundException
import com.example.api_tfg.model.Subscription
import com.example.api_tfg.model.SubscriptionType
import com.example.api_tfg.repository.UserRepository
import com.stripe.Stripe
import com.stripe.exception.SignatureVerificationException
import com.stripe.model.Customer
import com.stripe.model.Event
import com.stripe.model.checkout.Session
import com.stripe.net.Webhook
import com.stripe.param.checkout.SessionCreateParams
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Controlador que gestiona las operaciones relacionadas con pagos y suscripciones mediante Stripe.
 *
 * @property stripeSecretKey Clave secreta de Stripe para autenticación de la API.
 * @property successUrl URL a la que se redirige tras un pago exitoso.
 * @property cancelUrl URL a la que se redirige tras cancelar el pago.
 * @property endpointSecret Secreto para verificar la firma de los webhooks de Stripe.
 * @property monthlyPriceId ID de precio de Stripe para suscripción mensual.
 * @property oneTimePriceId ID de precio de Stripe para pago único.
 * @property userRepository Repositorio para acceso y actualización de usuarios.
 */
@Controller
@RequestMapping("/pay")
class PayController(
    @Value("\${STRIPE_SECRET_KEY}")
    private val stripeSecretKey: String,
    @Value("\${frontend.success.url}")
    private val successUrl: String,
    @Value("\${frontend.cancel.url}")
    private val cancelUrl: String,
    @Value("\${stripe.webhook.secret}")
    private val endpointSecret: String
) {

    @Value("\${stripe.price.monthly}")
    private lateinit var monthlyPriceId: String

    @Value("\${stripe.price.one_time}")
    private lateinit var oneTimePriceId: String

    @Autowired
    private lateinit var userRepository: UserRepository

    init {
        Stripe.apiKey = stripeSecretKey
    }

    /**
     * Crea una sesión de suscripción o pago único en Stripe y devuelve la URL para redirigir al checkout.
     *
     * @param request Datos de la suscripción, incluyendo el tipo y email del usuario.
     * @return Mapa con la URL de la sesión de pago de Stripe para la redirección.
     */
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

    /**
     * Controlador que recibe y procesa los webhooks enviados por Stripe.
     * Verifica la firma, procesa eventos tipo 'checkout.session.completed' y actualiza el rol del usuario a PREMIUM.
     *
     * @param payload Cuerpo JSON del webhook enviado por Stripe.
     * @param request Petición HTTP para obtener cabeceras (firma).
     * @return Respuesta HTTP con estado según el resultado del procesamiento.
     */
    @PostMapping("/webhook")
    fun handleStripeWebhook(@RequestBody payload: String, request: HttpServletRequest): ResponseEntity<String> {
        val sigHeader = request.getHeader("Stripe-Signature") ?: return ResponseEntity.badRequest().body("Falta la firma")

        val event = try {
            Webhook.constructEvent(payload, sigHeader, endpointSecret)
        } catch (e: Exception) {
            println("Error al verificar la firma del webhook: ${e.message}")
            return ResponseEntity.badRequest().body("Webhook error: ${e.message}")
        }

        if (event.type == "checkout.session.completed") {
            println("Webhook recibido: ${event.type}")
            val session = extractSession(event)

            session?.let {
                val email = it.customerEmail
                if (email != null) {
                    val user = userRepository.findUserBy_id(email)
                        .orElseThrow { NotFoundException("Usuario no encontrado con email: $email") }

                    user.roles = "PREMIUM"
                    userRepository.save(user)
                    println("Usuario $email actualizado a PREMIUM")
                }
            } ?: println("Sesión nula o deserialización fallida")
        }

        return ResponseEntity.ok("Evento procesado correctamente")
    }

    /**
     * Extrae la sesión de checkout de Stripe del evento recibido.
     *
     * @param event Evento recibido en el webhook de Stripe.
     * @return Objeto [Session] si la extracción fue exitosa, o null en caso contrario.
     */
    private fun extractSession(event: Event): Session? {
        val deserializer = event.dataObjectDeserializer
        return if (deserializer.`object`.isPresent) {
            deserializer.`object`.get() as? Session
        } else {
            Session.GSON.fromJson(deserializer.rawJson, Session::class.java)
        }
    }
}

