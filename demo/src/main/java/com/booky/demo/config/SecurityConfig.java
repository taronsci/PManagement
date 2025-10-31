package com.booky.demo.config;

import com.booky.demo.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login.html", "/user-profile.html", "/signup.html", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/api/user/login", "/api/user/signup","/api/user/check", "api/user/verify").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/api/user/login")
                        .successHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK))
//                        .failureHandler((req, res, ex) -> res.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                        .failureHandler((req, res, ex) -> {
                                    if (ex instanceof DisabledException) {
                                        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                                        res.setContentType("application/json");
//                                        res.getWriter().write("{\"error\": \"Account not verified. Please check your email.\"}");
                                    } else {
                                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                                        res.setContentType("application/json");
//                                        res.getWriter().write("{\"error\": \"Invalid username or password.\"}");
                                    }
                                })
                                .defaultSuccessUrl("/index.html",true)
                        .permitAll()
                ).userDetailsService(userService);

        return http.build();
    }

}
