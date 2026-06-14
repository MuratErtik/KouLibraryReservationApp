package org.koulibrary.koulibraryreservationapp.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Map;

import static org.koulibrary.koulibraryreservationapp.configs.RestApisConf.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(AUTHCONTROLLER + "/**").permitAll()

                        .requestMatchers(HttpMethod.GET,   USERCONTROLLER + "/me").authenticated()
                        .requestMatchers(HttpMethod.PATCH, USERCONTROLLER + "/me").authenticated()
                        .requestMatchers(HttpMethod.POST,  USERCONTROLLER + "/me/password").authenticated()
                        .requestMatchers(USERCONTROLLER + "/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, DESKCONTROLLER + "/admin/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST,   LIBRARYCONTROLLER + "/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH,  LIBRARYCONTROLLER + "/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, LIBRARYCONTROLLER + "/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET,    LIBRARYCONTROLLER + "/**").authenticated()

                        .requestMatchers(HttpMethod.GET, RESERVATIONCONTROLLER + "/me").authenticated()
                        .requestMatchers(HttpMethod.PATCH, RESERVATIONCONTROLLER + "/*/admin-cancel").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, RESERVATIONCONTROLLER).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, RESERVATIONCONTROLLER + "/*").hasRole("ADMIN")
                        .requestMatchers(RESERVATIONCONTROLLER + "/**").authenticated()

                        .requestMatchers(HttpMethod.GET, PENALTYCONTROLLER + "/me").authenticated()
                        .requestMatchers(PENALTYCONTROLLER + "/**").hasRole("ADMIN")

                        .requestMatchers(NOTIFICATIONCONTROLLER + "/**").authenticated()

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
                );
        return http.build();
    }

    //keycloak convert the realm roles (realm_access.roles) to authorities(ROLE_*)
    private JwtAuthenticationConverter jwtAuthConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null || !(realmAccess.get("roles") instanceof List<?> roles)) {
                return List.of();
            }
            return roles.stream()
                    .map(Object::toString)
                    .map(role -> "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .map(GrantedAuthority.class::cast)
                    .toList();
        });
        return converter;
    }

    @Bean

    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of("*"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE","PATCH", "OPTIONS"));

        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept","Origin","X-Requested-With","Cache-Control"));

        config.setAllowCredentials(true);

        config.setExposedHeaders(List.of("Authorization"));

        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;

    }
}
