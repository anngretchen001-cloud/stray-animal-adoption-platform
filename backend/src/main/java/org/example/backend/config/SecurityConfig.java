package org.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // 新的 DSL 风格禁用 CSRF
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // 所有请求放行
                );

        return http.build();
    }
}

