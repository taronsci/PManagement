package com.booky.demo.dto;

import com.booky.demo.model.Book;
import jakarta.validation.constraints.*;

public record BookListingDTO(
    Integer id,

    @NotNull(message = "Book ID is required")
    Integer bookId,

    Book book,
    Integer ownerId,
    String ownerUsername,

    @NotBlank(message = "Condition must be provided")
    @Pattern(regexp = "USED|NEW", message = "Condition type must be either USED or NEW")
    String condition,

    @NotBlank(message = "Transaction type is required")
    @Pattern(regexp = "SELL|RENT|EXCHANGE|GIVEAWAY", message = "Specify valid transaction type")
    String transactionType,

    String status,

    @Digits(integer = 4, fraction = 2, message = "Price must have up to 4 digits and 2 decimals")
    Double price,

    @Positive(message = "Rental duration must be positive")
    Integer rentalDuration
) {}
