package com.booky.demo.service;

import com.booky.demo.dao.BookDAO;
import com.booky.demo.dao.BookListingRepository;
import com.booky.demo.dao.UserDAO;
import com.booky.demo.dto.BookListingDTO;
import com.booky.demo.model.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class BookListingService {

    private final BookDAO bookDAO;
    private final UserDAO userDAO;

    private final BookListingRepository bookListingRepository;

    public BookListingService(BookDAO bookbookDAO,BookListingRepository repository, UserDAO userDAO) {
        this.bookDAO = bookbookDAO;
        this.bookListingRepository = repository;
        this.userDAO = userDAO;
    }

    @Transactional
    public Integer createListing(BookListingDTO dto, String name) {
        BookListing listing = new BookListing();
        listing.setBookId(dto.bookId());

        listing.setOwnerId(userDAO.getIdByUsername(name).get());

        listing.setCondition(BookCondition.valueOf(dto.condition()));
        listing.setTransactionType(TransactionType.valueOf(dto.transactionType()));
        listing.setStatus(RequestStatus.PENDING);

        BookListing saved = bookListingRepository.save(listing);

        if (dto.price() != null) {
            Details details = new Details();
            details.setBookListing(listing);
            details.setPrice(dto.price());
            listing.setDetails(details);

            if (dto.rentalDuration() != null ) {
                RentDetails rentDetails = new RentDetails();
                rentDetails.setDetails(details);
                rentDetails.setRentalDuration(dto.rentalDuration());
                details.setRentDetails(rentDetails);
            }
        }
        return saved.getId();
    }

    @Transactional
    public Page<BookListingDTO> getAllListings(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<BookListing> listings = bookListingRepository.findAllWithDetails(pageable);
        return listings.map(this::toDTO);
    }

    @Transactional
    public Page<BookListingDTO> getBookListingsById(String ownerName, int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        int ownerId = userDAO.getIdByUsername(ownerName).get();
        Page<BookListing> listings = bookListingRepository.findByIdWithDetails(ownerId, pageable);
        return listings.map(this::toDTO);
    }

    @Transactional
    public Page<BookListingDTO> filterBookListings(String query, String type, int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        if(type.equals("ALL"))
            type = null;
        if(query.isEmpty())
            query = null;

        Page<BookListing> listings = bookListingRepository.searchBooks(query, type, pageable);
        return listings.map(this::toDTO);
    }

    public void deleteListing(int id){
        bookListingRepository.deleteById(id);
    }

    BookListingDTO toDTO(BookListing listing) {
        Details details = listing.getDetails();
        RentDetails rentDetails =  null;
        if(listing.getTransactionType().equals(TransactionType.RENT))
            rentDetails = details.getRentDetails();

        return new BookListingDTO(
                listing.getId(),
                listing.getBookId(),
                bookDAO.findBookById(listing.getBookId()).get(),
                listing.getOwnerId(),
                userDAO.getUsernameById(listing.getOwnerId()).get(),
                listing.getCondition().getCode(),
                listing.getTransactionType().getCode(),
                listing.getStatus().getCode(),
                details != null ? details.getPrice() : null,
                rentDetails != null ? rentDetails.getRentalDuration() : null
        );
    }
}

