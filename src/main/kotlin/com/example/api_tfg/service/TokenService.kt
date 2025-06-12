package com.example.api_tfg.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.*

/**
 * Servicio para generar tokens JWT para usuarios autenticados.
 *
 * @property jwtEncoder Componente encargado de codificar y firmar el token JWT.
 */
@Service
class TokenService(
    @Autowired val jwtEncoder: JwtEncoder
) {

    /**
     * Genera un token JWT a partir de la información de autenticación del usuario.
     *
     * El token incluye:
     * - El emisor (issuer) como "self".
     * - La fecha y hora de emisión.
     * - La fecha de expiración, configurada a 365 días a partir de la emisión.
     * - El nombre de usuario (subject).
     * - Los roles del usuario en un claim llamado "roles".
     *
     * @param authentication Información de autenticación proporcionada por Spring Security.
     * @return El token JWT como cadena.
     */
    fun generarToken(authentication: Authentication) : String {

        println(authentication.authorities)

        val roles: String = authentication.authorities.joinToString(" ") { it.authority } // Contiene los roles del usuario

        val payload: JwtClaimsSet = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(Instant.now())
            .expiresAt(Date().toInstant().plus(Duration.ofDays(365)))
            .subject(authentication.name)
            .claim("roles", roles)
            .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(payload)).tokenValue
    }
}   