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

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Autowired
    private lateinit var rsaKeys: RSAKeysProperties

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

    @Bean
    fun passwordEncoder() : PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    /**
     * Método que inicializa un objeto de tipo AuthenticationManager
     */
    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration) : AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    /*
    MÉTODO PARA CODIFICAR UN JWT
     */
    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwk: JWK = RSAKey.Builder(rsaKeys.publicKey).privateKey(rsaKeys.privateKey).build()
        val jwks: JWKSource<SecurityContext> = ImmutableJWKSet(JWKSet(jwk))
        return NimbusJwtEncoder(jwks)
    }

    /*
    MÉTODO PARA DECODIFICAR UN JWT
     */
    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey).build()
    }




}
