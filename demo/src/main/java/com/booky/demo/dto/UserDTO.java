package com.booky.demo.dto;

import jakarta.validation.constraints.*;

public record UserDTO (
    int id,

    @NotBlank
    String username,

    @Email
    @NotBlank
    String email,

    @Size(min = 6, max = 20)
    String password
){}
