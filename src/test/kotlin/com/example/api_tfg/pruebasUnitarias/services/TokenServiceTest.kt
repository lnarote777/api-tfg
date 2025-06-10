package com.example.api_tfg.pruebasUnitarias.services

import com.example.api_tfg.service.TokenService
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import java.time.Instant

class TokenServiceTest {

    private lateinit var jwtEncoder: JwtEncoder
    private lateinit var tokenService: TokenService

    @BeforeEach
    fun setUp() {
        jwtEncoder = mockk()
        tokenService = TokenService(jwtEncoder)
    }

    @Test
    fun `generarToken should encode JWT with correct claims and return token value`() {
        val auth = mockk<Authentication>()
        val authority1 = mockk<GrantedAuthority>()
        val authority2 = mockk<GrantedAuthority>()

        every { auth.name } returns "user@example.com"
        every { auth.authorities } returns listOf(authority1, authority2)
        every { authority1.authority } returns "ROLE_USER"
        every { authority2.authority } returns "ROLE_ADMIN"

        // Capturar los parámetros
        val paramsSlot = slot<JwtEncoderParameters>()
        val mockJwt = mockk<Jwt>()
        every { mockJwt.tokenValue } returns "mocked.jwt.token"

        // Configurar correctamente el mock antes de llamar al método
        every { jwtEncoder.encode(capture(paramsSlot)) } returns mockJwt

        // Llamar al método
        val token = tokenService.generarToken(auth)

        // Validaciones
        assertEquals("mocked.jwt.token", token)
        verify(exactly = 1) { jwtEncoder.encode(any()) }

        // Validar claims
        val claimsSet = paramsSlot.captured.claims
        assertEquals("user@example.com", claimsSet.subject)
        assertEquals("self",claimsSet.getClaim("iss"))
        assertEquals("ROLE_USER ROLE_ADMIN", claimsSet.getClaim("roles"))

        assertNotNull(claimsSet.issuedAt)
        assertNotNull(claimsSet.expiresAt)
    }

}
