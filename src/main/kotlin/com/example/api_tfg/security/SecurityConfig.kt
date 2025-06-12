package com.example.api_tfg.security

import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.web.SecurityFilterChain

/**
 * Configuración de seguridad para la aplicación utilizando Spring Security.
 *
 * Define reglas de acceso, métodos de autenticación, generación y validación de JWT,
 * y configuración del cifrado de contraseñas.
 */
@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Autowired
    private lateinit var rsaKeys: RSAKeysProperties

    /**
     * Define el filtro de seguridad HTTP con reglas para cada endpoint.
     *
     * - Deshabilita CSRF.
     * - Define rutas públicas y protegidas.
     * - Configura OAuth2 Resource Server con JWT.
     * - Configura la política de sesiones como stateless (sin sesión).
     * - Habilita autenticación básica HTTP.
     *
     * @param http Objeto para construir la configuración HTTP.
     * @return Cadena de filtros de seguridad configurada.
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity) : SecurityFilterChain {

        return http
            .csrf { csrf -> csrf.disable() } // Cross-Site Forgery
            .authorizeHttpRequests { auth -> auth
                // Users
                .requestMatchers("/users/register", "/users/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/users/{username}").authenticated()
                .requestMatchers(HttpMethod.GET, "/users/list-users").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/users/delete").authenticated()
                .requestMatchers(HttpMethod.PUT, "/users/update").authenticated()

                // Daily Log
                .requestMatchers(HttpMethod.POST,"/daily-log/new/{email}").authenticated()
                .requestMatchers(HttpMethod.GET,"/daily-log").authenticated()
                .requestMatchers(HttpMethod.GET,"/daily-log/user/{userId}").authenticated()
                .requestMatchers(HttpMethod.GET,"/daily-log/user/{userId}/date/{date}").authenticated()
                .requestMatchers(HttpMethod.GET,"/daily-log/{id}").authenticated()
                .requestMatchers(HttpMethod.PUT,"/daily-log/user/{userId}/date/{date}").authenticated()
                .requestMatchers(HttpMethod.DELETE,"/daily-log/{id}").authenticated()

                //Cycle
                .requestMatchers(HttpMethod.POST,"/cycle/new").permitAll()
                .requestMatchers(HttpMethod.GET,"/cycle/user/{email}").permitAll()
                .requestMatchers(HttpMethod.GET,"/cycle/user/{email}/prediction").permitAll()
                .requestMatchers(HttpMethod.PUT,"/cycle/update").authenticated()
                .requestMatchers(HttpMethod.DELETE,"/cycle/{id}").authenticated()

                //Subscription
                .requestMatchers(HttpMethod.GET,"/pay/create-subscription").hasRole("USER")

                // Otros
                .anyRequest().permitAll()

            } // Los recursos protegidos y publicos
            .oauth2ResourceServer { oauth2 -> oauth2.jwt(Customizer.withDefaults()) }
            .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .httpBasic(Customizer.withDefaults())
            .build()

    }

    /**
     * Bean para cifrar contraseñas con BCrypt.
     *
     * @return PasswordEncoder configurado con BCrypt.
     */
    @Bean
    fun passwordEncoder() : PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    /**
     * Bean para obtener el AuthenticationManager que maneja la autenticación.
     *
     * @param authenticationConfiguration Configuración de autenticación de Spring.
     * @return AuthenticationManager para manejar autenticación.
     */
    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration) : AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    /**
     * Configura el encoder JWT con las claves RSA.
     *
     * Usa la clave privada para firmar los JWT.
     *
     * @return JwtEncoder configurado para firmar tokens.
     */
    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwk: JWK = RSAKey.Builder(rsaKeys.publicKey).privateKey(rsaKeys.privateKey).build()
        val jwks: JWKSource<SecurityContext> = ImmutableJWKSet(JWKSet(jwk))
        return NimbusJwtEncoder(jwks)
    }

    /**
     * Configura el decoder JWT para verificar tokens con la clave pública.
     *
     * @return JwtDecoder configurado para validar tokens firmados.
     */
    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey).build()
    }




}
