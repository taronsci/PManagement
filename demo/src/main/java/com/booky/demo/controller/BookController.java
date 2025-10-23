package com.booky.demo.controller;

import com.booky.demo.dto.BookDTO;
import com.booky.demo.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/book")
public class BookController {
    @Autowired
    private BookService bookService;

    @PostMapping
    public ResponseEntity<Integer> createBook(@Valid @RequestBody BookDTO book) {
        Integer bookId = bookService.save(book);
        return ResponseEntity.ok(bookId);
    }
}
