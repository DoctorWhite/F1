package team2.race_service.config;


import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Objects;

import static org.springframework.security.oauth2.core.authorization.OAuth2AuthorizationManagers.hasScope;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**", "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/h2-console", "/h2-console/**",
                                "/actuator/health", "/actuator/metrics", "/actuator/prometheus"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/races/**", "/race-results/**").access(hasScope("test_read"))
                        .requestMatchers(HttpMethod.POST, "/races/**", "/race-results/**").access(hasScope("test_write"))
                        .requestMatchers(HttpMethod.PUT, "/races/**", "/race-results/**").access(hasScope("test_write"))
                        .requestMatchers(HttpMethod.DELETE, "/races/**", "/race-results/**").access(hasScope("test_write"))
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }


    @Bean
    @ConditionalOnMissingBean(JwtDecoder.class)
    public JwtDecoder jwtDecoder(OAuth2ResourceServerProperties props) {
        String issuer = Objects.requireNonNull(props.getJwt().getIssuerUri());
        String audience = props.getJwt().getAudiences().getFirst();
        OAuth2TokenValidator<Jwt> validator = JwtValidators.createAtJwtValidator()
                .clientId(audience)
                .audience(audience)
                .issuer(issuer)
                .build();
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withIssuerLocation(issuer).build();
        decoder.setJwtValidator(validator);
        return decoder;
    }
}
