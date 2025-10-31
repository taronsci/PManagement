package com.booky.demo.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login.html", "/user-profile.html", "/signup.html", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/api/user/login", "/api/user/signup","/api/user/check").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/api/user/login")
                        .successHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK))
                        .failureHandler((req, res, ex) -> res.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                        .defaultSuccessUrl("/index.html",true)
                        .permitAll()
                );

        return http.build();
    }

}
