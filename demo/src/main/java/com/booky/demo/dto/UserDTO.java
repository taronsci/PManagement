package com.booky.demo.dto;

import com.booky.demo.validation.AUAEmail;
import jakarta.validation.constraints.*;

public record UserDTO (
    int id,

    @NotBlank
    String name,

    @NotBlank
    String surname,

    @NotBlank
    String username,

    @AUAEmail
    @NotBlank
    String email,

    @Size(min = 6, max = 20)
    String password
){}
