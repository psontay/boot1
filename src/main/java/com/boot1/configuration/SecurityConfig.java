package com.boot1.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import java.util.ArrayList;
import java.util.Collection;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final String[] PUBLIC_ENDPOINTS = { "/users/create" , "/auth/login" , "/auth/introspect" ,
            "/auth/logout" , "/auth/refresh"};
    @Value("${spring.jwt.signerKey}")
    private String signerKey;
    @Autowired
    private CustomJwtDecoder customJwtDecoder;
    @Bean
    public SecurityFilterChain filterChainer(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(authorizeRequests ->
                                                   authorizeRequests
                                                           .requestMatchers(HttpMethod.POST , PUBLIC_ENDPOINTS).permitAll()
                                                           .anyRequest().authenticated());
        httpSecurity.oauth2ResourceServer( config ->
                                         config.jwt( jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder)
                                                 .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                                                 .authenticationEntryPoint( new JWTAuthenticationEntryPoint())
                                                 .accessDeniedHandler(new CustomAccessDeniedHandler())
                                         );
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        return httpSecurity.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter( );
        converter.setJwtGrantedAuthoritiesConverter( jwt -> {
            Collection<GrantedAuthority> auths = new ArrayList<>();
            jwt.getClaimAsStringList("scope").stream()
                    .map( r -> "ROLE_" + r)
                    .map(SimpleGrantedAuthority::new)
                    .forEach(auths::add);
            jwt.getClaimAsStringList("permission").stream()
                    .map(SimpleGrantedAuthority::new)
                    .forEach(auths::add);
            return auths;
        });
        return converter;
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
