package rs.ua.bookstore.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
@Profile("!test")
class SecurityConfig {

    @Value("\${spring.security.oauth2.resource-server.jwt.jwk-set-uri}")
    private var jwkSetUri: String? = null

    @Autowired
    private lateinit var converter: JwtConverter

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http
            .oauth2ResourceServer { oauth2 -> oauth2.jwt { jwt -> jwt.jwtAuthenticationConverter(converter) } }
            .authorizeHttpRequests { requests ->
                requests.anyRequest().authenticated()
            }
            .cors { cors ->
                cors.configurationSource(corsConfigurationSource())
            }

            .csrf { csrf: CsrfConfigurer<HttpSecurity?>? -> csrf!!.disable() }
            .sessionManagement { session: SessionManagementConfigurer<HttpSecurity?>? ->
                session!!.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            }
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowCredentials = true
        configuration.allowedOriginPatterns = listOf("*")
        configuration.allowedHeaders = listOf("*")
        configuration.allowedMethods = listOf("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder
            .withJwkSetUri(jwkSetUri)
            .jwsAlgorithm(SignatureAlgorithm.RS256)
            .build()
    }
}

@Component
class JwtConverter : Converter<Jwt, AbstractAuthenticationToken> {

    private val jwtGrantedAuthoritiesConverter: JwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val authorities = jwtGrantedAuthoritiesConverter.convert(jwt)!! union extractResourceRoles(jwt)
        return JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt))
    }

    private fun getPrincipalClaimName(jwt: Jwt): String {
        val claimName = "preferred_username"
        return jwt.getClaim(claimName)
    }

    private fun extractResourceRoles(jwt: Jwt): Collection<GrantedAuthority?> {
        val resourceAccess: Map<String?, Any> = jwt.getClaim("resource_access")
        return ((resourceAccess["bookstore-client-id"] as? Map<*, *>)?.get("roles") as? List<*>)
            ?.map { role -> SimpleGrantedAuthority("ROLE_$role") }?.toSet()
            ?: emptySet<SimpleGrantedAuthority>()
    }
}

object Roles {
    const val ADMIN = "ADMIN"
    const val USER = "USER"
}