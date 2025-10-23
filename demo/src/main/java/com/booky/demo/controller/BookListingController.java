package com.booky.demo.controller;

import com.booky.demo.dto.BookListingDTO;
import com.booky.demo.service.BookListingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("/api/listing")
public class BookListingController {
    private final BookListingService bookListingService;
    private final PagedResourcesAssembler<BookListingDTO> assembler;

    public BookListingController(BookListingService bookListingService, PagedResourcesAssembler<BookListingDTO> assembler) {
        this.bookListingService = bookListingService;
        this.assembler = assembler;
    }

    @PostMapping("/create")
    public ResponseEntity<Integer> createListing(@Valid @RequestBody BookListingDTO listingDTO,
                                                 Principal principal) {
        String name = principal.getName();
        Integer listingId = bookListingService.createListing(listingDTO, name);
        return ResponseEntity.ok(listingId);
    }

    @GetMapping
    public PagedModel<EntityModel<BookListingDTO>> getAllBookListings(@RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "9") int size) {
        Page<BookListingDTO> listingPage = bookListingService.getAllListings(page, size);
        return assembler.toModel(listingPage);
    }

    @GetMapping("/my")
    public PagedModel<EntityModel<BookListingDTO>> getBooksListingsById(Principal principal,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "9") int size) {
        String name = principal.getName();
        Page<BookListingDTO> listingPage = bookListingService.getBookListingsById(name, page, size);
        return assembler.toModel(listingPage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListing(@PathVariable int id) {
        bookListingService.deleteListing(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public PagedModel<EntityModel<BookListingDTO>> getBooksListingsById(@RequestParam(defaultValue = "") String query,
                                                                        @RequestParam String type,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "9") int size) {
        Page<BookListingDTO> listingPage = bookListingService.filterBookListings(query, type, page, size);
        return assembler.toModel(listingPage);
    }
}
