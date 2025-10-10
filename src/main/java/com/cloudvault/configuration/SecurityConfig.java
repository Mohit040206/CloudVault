package com.cloudvault.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for file upload
                .csrf(csrf -> csrf.disable())

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
//                        // Allow POST for file upload
//                        .requestMatchers(HttpMethod.GET, "/documents/upload").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/documents/upload").permitAll()

                        // Allow GET requests for gallery, view, images, static content, etc.
                        .requestMatchers(
                                "/**","/user/register",
                                "/documents/view/**",
                                "/user/change-password",
                                "/forgot-password/**",
                                "/forgotPassword",
                                "/documents/download/**",
                                "/documents/search",
                                "/documents/delete/**",
                                "/upload",
                                "/about",
                                "/change_password","/documents/upload","/documents/gallery","/user/login",
                                "/user/home", "/index","/register", "/user/login","/login","/gallery","/images/**", "/css/**", "/js/**"
                        ).permitAll()
//                        .requestMatchers("/documents/upload").permitAll() // both GET and POST require login


                        // All other requests require authentication
                        .anyRequest().authenticated()
                )

                // Disable default Spring login page
                .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
