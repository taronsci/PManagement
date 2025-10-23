package com.booky.demo.dto;

import com.booky.demo.model.Book;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookRequestDTO(
    Integer id,

    String requesterUsername,

    @NotNull
    Integer listingId,

    String status,

    Book book,
    String ownerUsername,

    @NotNull
    @PastOrPresent
    LocalDateTime createdAt,

    LocalDate rentalStartDate
){}
