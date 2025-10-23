package com.booky.demo.service;

import com.booky.demo.dao.*;
import com.booky.demo.dto.BookRequestDTO;
import com.booky.demo.model.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookRequestServiceTest {
    @Mock
    private BookRequestRepository bookRequestRepository;

    @Mock
    private BookRequestDAO bookRequestDAO;

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private BookRequestService bookRequestService;

    //create request
    @Test
    void createRequest_successful_returnsSavedId() {
        // Arrange
        String username = "alice";
        Integer userId = 42;

        BookRequestDTO dto = new BookRequestDTO(
                null,                // id
                null,                   // requesterUsername
                100,                    // listingId
                null,                   // status
                null,                   // book
                null,                   // ownerUsername
                LocalDateTime.now(),    // createdAt
                null                    // rentalStartDate
        );

        BookRequest savedRequest = new BookRequest();
        savedRequest.setId(1);

        when(userDAO.getIdByUsername(username)).thenReturn(Optional.of(userId));
        when(bookRequestRepository.save(any(BookRequest.class))).thenReturn(savedRequest);

        Integer result = bookRequestService.createRequest(dto, username);

        assertEquals(1, result);

        verify(bookRequestRepository).save(argThat(request ->
                request.getRequesterId().equals(userId) &&
                        request.getListingId().equals(100) &&
                        request.getStatus() == RequestStatus.PENDING &&
                        request.getCreatedAt().equals(dto.createdAt())
        ));
    }

    @Test
    void createRequest_userNotFound_throwsException() {
        String username = "ghost";
        BookRequestDTO dto = new BookRequestDTO(
                null, null, 100, null, null, null, LocalDateTime.now(), null
        );

        when(userDAO.getIdByUsername(username)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () ->
                bookRequestService.createRequest(dto, username)
        );

        verify(bookRequestRepository, never()).save(any());
    }

    //getBookRequestsById
    @Test
    void getBookRequestsById_returnsPageFromDAO() {
        String ownerName = "alice";
        int page = 0;
        int size = 2;
        int ownerId = 42;

        when(userDAO.getIdByUsername(ownerName)).thenReturn(Optional.of(ownerId));

        BookRequestDTO dto1 = new BookRequestDTO(1, "alice", 100, "PENDING", null, null, LocalDateTime.now(), null);
        BookRequestDTO dto2 = new BookRequestDTO(2, "alice", 101, "PENDING", null, null, LocalDateTime.now(), null);

        Page<BookRequestDTO> pageResult = new PageImpl<>(List.of(dto1, dto2));

        when(bookRequestDAO.findRequests(ownerId, PageRequest.of(page, size)))
                .thenReturn(pageResult);

        Page<BookRequestDTO> result = bookRequestService.getBookRequestsById(ownerName, page, size);

        assertEquals(2, result.getTotalElements());
        assertEquals(dto1, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));

        verify(userDAO).getIdByUsername(ownerName);
        verify(bookRequestDAO).findRequests(ownerId, PageRequest.of(page, size));
    }

    @Test
    void getBookRequestsById_userNotFound_throwsException() {
        String ownerName = "ghost";
        int page = 0;
        int size = 2;

        when(userDAO.getIdByUsername(ownerName)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () ->
                bookRequestService.getBookRequestsById(ownerName, page, size)
        );

        verify(bookRequestDAO, never()).findRequests(anyInt(), any());
    }

    //getMyRequests
    @Test
    void getMyRequests_returnsPageFromDAO() {
        String ownerName = "alice";
        int page = 0;
        int size = 2;
        int ownerId = 42;

        when(userDAO.getIdByUsername(ownerName)).thenReturn(Optional.of(ownerId));

        BookRequestDTO dto1 = new BookRequestDTO(1, "alice", 100, "PENDING", null, null, LocalDateTime.now(), null);
        BookRequestDTO dto2 = new BookRequestDTO(2, "alice", 101, "PENDING", null, null, LocalDateTime.now(), null);

        Page<BookRequestDTO> pageResult = new PageImpl<>(List.of(dto1, dto2));

        when(bookRequestDAO.findMyRequests(ownerId, PageRequest.of(page, size)))
                .thenReturn(pageResult);

        Page<BookRequestDTO> result = bookRequestService.getMyRequests(ownerName, page, size);

        assertEquals(2, result.getTotalElements());
        assertEquals(dto1, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));

        verify(userDAO).getIdByUsername(ownerName);
        verify(bookRequestDAO).findMyRequests(ownerId, PageRequest.of(page, size));
    }

    @Test
    void getMyRequests_userNotFound_throwsException() {
        String ownerName = "ghost";
        int page = 0;
        int size = 2;

        when(userDAO.getIdByUsername(ownerName)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () ->
                bookRequestService.getMyRequests(ownerName, page, size)
        );

        verify(bookRequestDAO, never()).findMyRequests(anyInt(), any());
    }

    //deleteRequest
    @Test
    void deleteRequest_returnsTrue_whenDAODeletes() {
        int id = 42;
        when(bookRequestDAO.deleteById(id)).thenReturn(1);

        boolean result = bookRequestService.deleteRequest(id);

        assertTrue(result);
        verify(bookRequestDAO).deleteById(id);
    }

    @Test
    void deleteRequest_returnsFalse_whenDAODeletesNothing() {
        int id = 100;
        when(bookRequestDAO.deleteById(id)).thenReturn(0);

        boolean result = bookRequestService.deleteRequest(id);

        assertFalse(result);
        verify(bookRequestDAO).deleteById(id);
    }

    @Test
    void deleteRequest_whenDAOThrowsException_propagatesException() {
        int id = 200;
        when(bookRequestDAO.deleteById(id)).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> bookRequestService.deleteRequest(id));
        verify(bookRequestDAO).deleteById(id);
    }

    //updateStatus
    @Test
    void updateStatus_returnsTrue_whenDAOUpdates() {
        int id = 42;
        RequestStatus status = RequestStatus.ACCEPTED;
        when(bookRequestDAO.updateStatus(id, status)).thenReturn(1);

        boolean result = bookRequestService.updateStatus(id, status);

        assertTrue(result);
        verify(bookRequestDAO).updateStatus(id, status);
    }

    @Test
    void updateStatus_returnsFalse_whenDAOUpdatesNothing() {
        int id = 100;
        RequestStatus status = RequestStatus.REJECTED;
        when(bookRequestDAO.updateStatus(id, status)).thenReturn(0);

        boolean result = bookRequestService.updateStatus(id, status);

        assertFalse(result);
        verify(bookRequestDAO).updateStatus(id, status);
    }

    @Test
    void updateStatus_whenDAOThrowsException_propagatesException() {
        int id = 200;
        RequestStatus status = RequestStatus.PENDING;
        when(bookRequestDAO.updateStatus(id, status)).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> bookRequestService.updateStatus(id, status));
        verify(bookRequestDAO).updateStatus(id, status);
    }

}
