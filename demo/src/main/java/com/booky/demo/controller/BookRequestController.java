package com.booky.demo.controller;

import com.booky.demo.dto.BookRequestDTO;
import com.booky.demo.model.RequestStatus;
import com.booky.demo.service.BookRequestService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
//import java.security.Principal;

@RestController
@RequestMapping("/api/request")
public class BookRequestController {
    private final BookRequestService bookRequestService;
    private final PagedResourcesAssembler<BookRequestDTO> assembler;

    public BookRequestController(BookRequestService bookRequestService, PagedResourcesAssembler<BookRequestDTO> assembler) {
        this.bookRequestService = bookRequestService;
        this.assembler = assembler;
    }

    @PostMapping
    public ResponseEntity<Integer> createRequest(@Valid @RequestBody BookRequestDTO requestDTO,
                                                 Principal principal) {

        Integer listingId = bookRequestService.createRequest(requestDTO, principal.getName());
        return ResponseEntity.ok(listingId);
    }

    @GetMapping("/myReq")
    public PagedModel<EntityModel<BookRequestDTO>> getBooksRequestsById(Principal principal,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "3") int size) {
        String name = principal.getName();
        Page<BookRequestDTO> requestPage = bookRequestService.getBookRequestsById(name, page, size);
        return assembler.toModel(requestPage);
    }

    @GetMapping("/my")
    public PagedModel<EntityModel<BookRequestDTO>> getMyRequests(Principal principal,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "3") int size) {
        String name = principal.getName();
        Page<BookRequestDTO> requestPage = bookRequestService.getMyRequests(name, page, size);
        return assembler.toModel(requestPage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable int id) {
        boolean deleted = bookRequestService.deleteRequest(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status/{accepted}")
    public ResponseEntity<Void> updateStatus(@PathVariable int id,
                                             @PathVariable String accepted) {
        boolean isAccepted = Boolean.parseBoolean(accepted);
        RequestStatus status = isAccepted ? RequestStatus.ACCEPTED : RequestStatus.REJECTED;
        boolean updated = bookRequestService.updateStatus(id, status);
        return updated ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
