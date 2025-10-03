package rs.ua.bookstore.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import java.time.Instant

@TestConfiguration
//@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
@Profile("test")
class TestSecurityConfig {

    @Bean
    @Primary
    fun testSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth.anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt ->
                    jwt.jwtAuthenticationConverter(testJwtAuthenticationConverter())
                }
            }
        return http.build()
    }

    @Bean
    @Primary
    fun testJwtDecoder(): JwtDecoder {
        return JwtDecoder { token ->
            val roles = when {
                token.contains("ADMIN") -> listOf("ADMIN", "USER")
                token.contains("USER") -> listOf("USER")
                else -> emptyList()
            }

            Jwt.withTokenValue(token)
                .header("alg", "none")
                .claim("roles", roles)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build()
        }
    }

    private fun testJwtAuthenticationConverter(): JwtAuthenticationConverter {
        val converter = JwtAuthenticationConverter()
        converter.setJwtGrantedAuthoritiesConverter { jwt ->
            val roles = jwt.getClaimAsStringList("roles") ?: emptyList()
            roles.map { SimpleGrantedAuthority(it) }
        }
        return converter
    }
}