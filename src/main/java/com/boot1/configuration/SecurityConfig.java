package com.boot1.configuration;


import com.boot1.enums.*;
import com.boot1.enums.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final String[] PUBLIC_ENDPOINTS = { "/users/create" , "/auth/login" , "/auth/introspect"};
    @Value("${spring.jwt.signerKey}")
    private String signerKey;
    @Bean
    public SecurityFilterChain filterChainer(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(authorizeRequests ->
                                                   authorizeRequests
                                                           .requestMatchers(HttpMethod.POST , PUBLIC_ENDPOINTS).permitAll()
                                                           .anyRequest().authenticated());
        httpSecurity.oauth2ResourceServer( config ->
                                         config.jwt( jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())
                                                 .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                                                 .authenticationEntryPoint( new JWTAuthenticationEntryPoint())
                                                 .accessDeniedHandler(new CustomAccessDeniedHandler())
                                         );
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        return httpSecurity.build();
    }
    @Bean
    JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
