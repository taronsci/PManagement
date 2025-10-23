package com.booky.demo.service;

import com.booky.demo.dao.BookDAO;
import com.booky.demo.dao.BookListingRepository;
import com.booky.demo.dao.UserDAO;
import com.booky.demo.dto.BookListingDTO;
import com.booky.demo.model.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookListingServiceTest {
    @Mock
    private BookDAO bookDAO;

    @Mock
    private UserDAO userDAO;

    @Mock
    private BookListingRepository bookListingRepository;

    @InjectMocks
    private BookListingService bookListingService;

    //createListing
    @Test
    void createListing_basic_returnsSavedId() {
        BookListingDTO dto = new BookListingDTO(
                null,         // id
                10,              // bookId
                null,            // book
                null,            // ownerId
                null,            // ownerUsername
                "NEW",           // condition
                "SELL",          // transactionType
                null,            // status
                null,            // price
                null             // rentalDuration
        );
        String username = "taronsci";

        BookListing savedListing = new BookListing();
        savedListing.setId(42);

        when(userDAO.getIdByUsername(username)).thenReturn(Optional.of(7));
        when(bookListingRepository.save(any(BookListing.class))).thenReturn(savedListing);

        Integer result = bookListingService.createListing(dto, username);

        assertEquals(42, result);
        verify(bookListingRepository).save(argThat(listing ->
                listing.getBookId() == 10 &&
                        listing.getOwnerId() == 7 &&
                        listing.getCondition() == BookCondition.NEW &&
                        listing.getTransactionType() == TransactionType.SELL &&
                        listing.getStatus() == RequestStatus.PENDING
        ));
    }

    @Test
    void createListing_withPrice_createsDetailsWithoutRentDetails() {
        BookListingDTO dto = new BookListingDTO(
                null,
                20,
                null,
                null,
                null,
                "USED",
                "SELL",
                null,
                15.99,
                null
        );
        String username = "Cory";

        BookListing savedListing = new BookListing();
        savedListing.setId(100);

        when(userDAO.getIdByUsername(username)).thenReturn(Optional.of(8));
        when(bookListingRepository.save(any(BookListing.class))).thenReturn(savedListing);

        Integer result = bookListingService.createListing(dto, username);

        assertEquals(100, result);
        verify(bookListingRepository).save(argThat(listing ->
                listing.getBookId() == 20 &&
                        listing.getOwnerId() == 8 &&
                        listing.getDetails() != null &&
                        listing.getDetails().getPrice().equals(15.99) &&
                        listing.getDetails().getRentDetails() == null
        ));
    }

    @Test
    void createListing_withPriceAndRentalDuration_createsDetailsAndRentDetails() {
        BookListingDTO dto = new BookListingDTO(
                null,
                30,
                null,
                null,
                null,
                "NEW",
                "RENT",
                null,
                9.99,
                14
        );
        String username = "Carol";

        BookListing savedListing = new BookListing();
        savedListing.setId(200);

        when(userDAO.getIdByUsername(username)).thenReturn(Optional.of(9));
        when(bookListingRepository.save(any(BookListing.class))).thenReturn(savedListing);

        Integer result = bookListingService.createListing(dto, username);

        assertEquals(200, result);
        verify(bookListingRepository).save(argThat(listing ->
                listing.getBookId() == 30 &&
                        listing.getOwnerId() == 9 &&
                        listing.getDetails() != null &&
                        listing.getDetails().getPrice().equals(9.99) &&
                        listing.getDetails().getRentDetails() != null &&
                        listing.getDetails().getRentDetails().getRentalDuration() == 14
        ));
    }

    //getAllListings
    @Test
    void getAllListings_returnsMappedPage() {
        int page = 0;
        int size = 2;

        BookListing listing1 = new BookListing();
        listing1.setId(1);
        listing1.setBookId(10);
        listing1.setOwnerId(100);

        BookListing listing2 = new BookListing();
        listing2.setId(2);
        listing2.setBookId(20);
        listing2.setOwnerId(200);

        List<BookListing> listings = List.of(listing1, listing2);
        Page<BookListing> pageResult = new PageImpl<>(listings);

        when(bookListingRepository.findAllWithDetails(PageRequest.of(page, size)))
                .thenReturn(pageResult);

        BookListingService spyService = spy(bookListingService);

        BookListingDTO dto1 = new BookListingDTO(1, 10, null, 100, null, "USED", "SELL", "PENDING", 12.99, null);
        BookListingDTO dto2 = new BookListingDTO(2, 20, null, 200, null, "NEW", "RENT", "PENDING", null, 7);

        doReturn(dto1).when(spyService).toDTO(listing1);
        doReturn(dto2).when(spyService).toDTO(listing2);

        Page<BookListingDTO> result = spyService.getAllListings(page, size);

        assertEquals(2, result.getTotalElements());
        assertEquals(dto1, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));

        verify(bookListingRepository).findAllWithDetails(PageRequest.of(page, size));
        verify(spyService).toDTO(listing1);
        verify(spyService).toDTO(listing2);
    }

    //getBookListingsById
    @Test
    void getBookListingsById_returnsMappedPage() {
        String ownerName = "alice";
        int page = 0;
        int size = 2;
        int ownerId = 99;

        when(userDAO.getIdByUsername(ownerName)).thenReturn(Optional.of(ownerId));

        BookListing listing1 = new BookListing();
        listing1.setId(1);
        listing1.setBookId(10);
        listing1.setOwnerId(ownerId);

        BookListing listing2 = new BookListing();
        listing2.setId(2);
        listing2.setBookId(20);
        listing2.setOwnerId(ownerId);

        List<BookListing> listingList = List.of(listing1, listing2);
        Page<BookListing> pageResult = new PageImpl<>(listingList);

        when(bookListingRepository.findByIdWithDetails(ownerId, PageRequest.of(page, size)))
                .thenReturn(pageResult);

        BookListingService spyService = spy(bookListingService);

        BookListingDTO dto1 = new BookListingDTO(1, 10, null, ownerId, "alice", "USED", "SELL", "PENDING", 12.99, null);
        BookListingDTO dto2 = new BookListingDTO(2, 20, null, ownerId, "alice", "NEW", "RENT", "PENDING", null, 7);

        doReturn(dto1).when(spyService).toDTO(listing1);
        doReturn(dto2).when(spyService).toDTO(listing2);

        Page<BookListingDTO> result = spyService.getBookListingsById(ownerName, page, size);

        assertEquals(2, result.getTotalElements());
        assertEquals(dto1, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));

        verify(userDAO).getIdByUsername(ownerName);
        verify(bookListingRepository).findByIdWithDetails(ownerId, PageRequest.of(page, size));
        verify(spyService).toDTO(listing1);
        verify(spyService).toDTO(listing2);
    }

    @Test
    void getBookListingsById_userNotFound_throwsException() {
        String ownerName = "ghost";
        when(userDAO.getIdByUsername(ownerName)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                bookListingService.getBookListingsById(ownerName, 0, 5)
        );

        verify(bookListingRepository, never()).findByIdWithDetails(anyInt(), any());
    }

    //filterBookListings
    @Test
    void filterBookListings_withValidQueryAndType_callsRepositoryCorrectly() {
        String query = "Dune";
        String type = "SELL";
        int page = 0;
        int size = 2;

        BookListing listing = new BookListing();
        listing.setId(1);
        listing.setBookId(10);

        Page<BookListing> pageResult = new PageImpl<>(List.of(listing));

        when(bookListingRepository.searchBooks(query, type, PageRequest.of(page, size)))
                .thenReturn(pageResult);

        BookListingService spyService = spy(bookListingService);
        BookListingDTO dto = new BookListingDTO(1, 10, null, null, null, "USED", type, "PENDING", 9.99, null);
        doReturn(dto).when(spyService).toDTO(listing);

        Page<BookListingDTO> result = spyService.filterBookListings(query, type, page, size);

        assertEquals(1, result.getTotalElements());
        assertEquals(dto, result.getContent().get(0));

        verify(bookListingRepository).searchBooks(query, type, PageRequest.of(page, size));
        verify(spyService).toDTO(listing);
    }

    @Test
    void filterBookListings_withAllType_setsTypeToNull() {
        String query = "Book Title";
        String type = "ALL";
        int page = 0;
        int size = 5;

        BookListing listing = new BookListing();
        listing.setId(2);
        Page<BookListing> pageResult = new PageImpl<>(List.of(listing));

        when(bookListingRepository.searchBooks(query, null, PageRequest.of(page, size)))
                .thenReturn(pageResult);

        BookListingService spyService = spy(bookListingService);
        BookListingDTO dto = new BookListingDTO(2, 5, null, null, null, "NEW", "SELL", "PENDING", 12.5, null);
        doReturn(dto).when(spyService).toDTO(listing);

        Page<BookListingDTO> result = spyService.filterBookListings(query, type, page, size);

        assertEquals(1, result.getTotalElements());
        assertEquals(dto, result.getContent().get(0));

        verify(bookListingRepository).searchBooks(query, null, PageRequest.of(page, size));
    }

    @Test
    void filterBookListings_withEmptyQuery_setsQueryToNull() {
        String query = "";
        String type = "RENT";
        int page = 0;
        int size = 3;

        BookListing listing = new BookListing();
        listing.setId(3);
        Page<BookListing> pageResult = new PageImpl<>(List.of(listing));

        when(bookListingRepository.searchBooks(null, type, PageRequest.of(page, size)))
                .thenReturn(pageResult);

        BookListingService spyService = spy(bookListingService);
        BookListingDTO dto = new BookListingDTO(3, 7, null, null, null, "USED", type, "PENDING", null, 10);
        doReturn(dto).when(spyService).toDTO(listing);

        Page<BookListingDTO> result = spyService.filterBookListings(query, type, page, size);

        assertEquals(1, result.getTotalElements());
        assertEquals(dto, result.getContent().get(0));

        verify(bookListingRepository).searchBooks(null, type, PageRequest.of(page, size));
    }

    //deleteListing
    @Test
    void deleteListing_callsRepositoryDeleteById() {
        int id = 42;

        bookListingService.deleteListing(id);

        verify(bookListingRepository).deleteById(id);
    }

    @Test
    void deleteListing_whenRepositoryThrowsException_propagatesException() {
        int id = 100;
        doThrow(new RuntimeException("Database error"))
                .when(bookListingRepository).deleteById(id);

        assertThrows(RuntimeException.class, () ->
                bookListingService.deleteListing(id)
        );

        verify(bookListingRepository).deleteById(id);
    }

    //toDTO
    @Test
    void toDTO_sellListing_mapsCorrectly() {
        BookListing listing = new BookListing();
        listing.setId(1);
        listing.setBookId(100);
        listing.setOwnerId(50);
        listing.setCondition(BookCondition.NEW);
        listing.setTransactionType(TransactionType.SELL);
        listing.setStatus(RequestStatus.PENDING);

        Details details = new Details();
        details.setPrice(15.99);
        listing.setDetails(details);

        Book book = new Book();
        book.setId(100);
        book.setTitle("Test Book");

        when(bookDAO.findBookById(100)).thenReturn(Optional.of(book));
        when(userDAO.getUsernameById(50)).thenReturn(Optional.of("alice"));

        // Act
        BookListingDTO dto = bookListingService.toDTO(listing);

        // Assert
        assertEquals(1, dto.id());
        assertEquals(100, dto.bookId());
        assertEquals(book, dto.book());
        assertEquals(50, dto.ownerId());
        assertEquals("alice", dto.ownerUsername());
        assertEquals(BookCondition.NEW.getCode(), dto.condition());
        assertEquals(TransactionType.SELL.getCode(), dto.transactionType());
        assertEquals(RequestStatus.PENDING.getCode(), dto.status());
        assertEquals(15.99, dto.price());
        assertNull(dto.rentalDuration());
    }

    @Test
    void toDTO_rentListing_includesRentDetails() {
        BookListing listing = new BookListing();
        listing.setId(2);
        listing.setBookId(200);
        listing.setOwnerId(60);
        listing.setCondition(BookCondition.USED);
        listing.setTransactionType(TransactionType.RENT);
        listing.setStatus(RequestStatus.PENDING);

        Details details = new Details();
        details.setPrice(9.99);

        RentDetails rentDetails = new RentDetails();
        rentDetails.setRentalDuration(14);
        details.setRentDetails(rentDetails);

        listing.setDetails(details);

        Book book = new Book();
        book.setId(200);
        book.setTitle("Rent Book");

        when(bookDAO.findBookById(200)).thenReturn(Optional.of(book));
        when(userDAO.getUsernameById(60)).thenReturn(Optional.of("bob"));

        BookListingDTO dto = bookListingService.toDTO(listing);

        assertEquals(2, dto.id());
        assertEquals(200, dto.bookId());
        assertEquals(book, dto.book());
        assertEquals(60, dto.ownerId());
        assertEquals("bob", dto.ownerUsername());
        assertEquals(BookCondition.USED.getCode(), dto.condition());
        assertEquals(TransactionType.RENT.getCode(), dto.transactionType());
        assertEquals(RequestStatus.PENDING.getCode(), dto.status());
        assertEquals(9.99, dto.price());
        assertEquals(14, dto.rentalDuration());
    }

    @Test
    void toDTO_withNullDetails_returnsNullPriceAndRentalDuration() {
        BookListing listing = new BookListing();
        listing.setId(3);
        listing.setBookId(300);
        listing.setOwnerId(70);
        listing.setCondition(BookCondition.NEW);
        listing.setTransactionType(TransactionType.SELL);
        listing.setStatus(RequestStatus.ACCEPTED);

        listing.setDetails(null);

        Book book = new Book();
        book.setId(300);
        book.setTitle("No Details Book");

        when(bookDAO.findBookById(300)).thenReturn(Optional.of(book));
        when(userDAO.getUsernameById(70)).thenReturn(Optional.of("carol"));

        BookListingDTO dto = bookListingService.toDTO(listing);

        assertEquals(3, dto.id());
        assertEquals(300, dto.bookId());
        assertEquals(book, dto.book());
        assertEquals(70, dto.ownerId());
        assertEquals("carol", dto.ownerUsername());
        assertEquals(BookCondition.NEW.getCode(), dto.condition());
        assertEquals(TransactionType.SELL.getCode(), dto.transactionType());
        assertEquals(RequestStatus.ACCEPTED.getCode(), dto.status());
        assertNull(dto.price());
        assertNull(dto.rentalDuration());
    }

}
