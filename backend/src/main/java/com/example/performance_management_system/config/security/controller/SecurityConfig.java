package com.example.performance_management_system.config.security.controller;

import com.example.performance_management_system.config.security.filter.JwtAuthenticationFilter;
import com.example.performance_management_system.config.security.jwt.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtUtil jwtUtil) throws Exception {

        http
                // 🔥 ENABLE CORS
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())

                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .authorizeHttpRequests(auth -> auth
                        // 🔥 allow preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 🔓 Everyone logged in can VIEW active cycle
                        .requestMatchers(
                                "/api/performance-cycles/active-cycle"
                        ).authenticated()

                        // 🔐 HR & ADMIN can manage cycles
                        .requestMatchers(
                                "/api/performance-cycles"
                        ).hasAnyRole("HR", "ADMIN")

                        // 🔐 HR only actions
                        .requestMatchers(
                                "/api/performance-cycles/*/start",
                                "/api/performance-cycles/*/close"
                        ).hasRole("HR")

                        // 🔐 Goals
                        .requestMatchers("/api/goals/**")
                        .hasAnyRole("EMPLOYEE", "MANAGER")

                        // 🔐 Continuous feedback
                        .requestMatchers("/api/feedback/**")
                        .hasAnyRole("EMPLOYEE", "MANAGER", "HR")
                        // 🔓 public auth endpoints
                        .requestMatchers("/api/auth/**").permitAll()

                        // 🔐 HR & ADMIN only
                        .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "HR")

                        // 🔐 everything else requires auth
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
