package com.booky.demo.controller;

import com.booky.demo.dto.UserDTO;
import com.booky.demo.model.User;
import com.booky.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> create(@Valid @RequestBody UserDTO user) {
        Optional<Integer> userId = userService.register(user);

        if(userId.isEmpty())
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username is not available");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userId.get());
    }

    @GetMapping("/verify")
    public RedirectView verify(@RequestParam("token") String token) {
        RedirectView redirectView = new RedirectView();

        if (userService.verifyToken(token)) {
            redirectView.setUrl("http://localhost:8080/login.html?status=success");
        } else {
            redirectView.setUrl("http://localhost:8080/login.html?status=error");
        }
        return redirectView;
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAuthenticated = auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);

        if (!isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("authenticated", false));
        }

        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "username", auth.getName()
        ));
    }

    @PatchMapping("/update")
    public ResponseEntity<UserDTO> updateProfile(@RequestBody User user,
                                                 Principal principal) {
        String name = principal.getName();
        return userService.updateProfile(user, name);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        userService.logout(request);
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = authentication.getName();
        UserDTO user = userService.findUserByUsername(username);

        return ResponseEntity.ok(Map.of(
                "username", user.username(),
                "email", user.email()
        ));
    }
}
