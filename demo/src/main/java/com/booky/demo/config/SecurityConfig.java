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
                        .requestMatchers("/", "/static/login.html", "/static/index.html", "/static/user-profile.html", "/static/signup.html", "/static/css/**", "/static/js/**").permitAll()
                        .requestMatchers("/api/user/login", "/api/user/signup","/api/user/check").permitAll()
                        .requestMatchers("/api/listing", "/api/listing/search").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/static/login.html")
                        .loginProcessingUrl("/api/user/login")
                        .successHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK))
                        .failureHandler((req, res, ex) -> res.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                        .defaultSuccessUrl("/static/index.html",true)
                        .permitAll()
                );

        return http.build();
    }

}
