package com.backendSupermercado.supermercasdo.config.securityconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.backendSupermercado.supermercasdo.security.filter.JwtAuthFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http) throws Exception {
            http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/auth/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
                )
                .sessionManagement(session -> session
                    .sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                    )
                );
        //filtro jwt
        http.addFilterBefore(
            jwtAuthFilter, 
            UsernamePasswordAuthenticationFilter.class
        );
        return http.build();
    }
}
