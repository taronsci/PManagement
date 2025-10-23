package com.booky.demo.dto;


import com.booky.demo.validation.MaxCurrentYear;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookDTO(
    int id,

    @NotBlank(message = "Title must not be blank")
    @Size(max = 200, message = "That title is too long")
    String title,

    @NotBlank(message = "A book must have an author")
    String author,

    @Min(value = 1700, message = "We suggest taking your book to a library if you have something older than this")
    @MaxCurrentYear
    Integer year,

    @NotBlank(message = "Genre must not be blank")
    @Size(max = 50, message = "Genre must be at most 50 characters")
    String genre
){}
