package com.booky.demo.service;

import com.booky.demo.dao.BookDAO;
import com.booky.demo.dto.BookDTO;
import com.booky.demo.model.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookDAO bookDAO;

    @InjectMocks
    private BookService bookService;


    @Test
    void save_existingBook_returnsExistingId() {
        BookDTO book = new BookDTO(1, "1984", "George Orwell", 1949,"Dystopian");
        when(bookDAO.findByTitleAndAuthor("1984", "George Orwell")).thenReturn(Optional.of(1));

        Integer result = bookService.save(book);

        assertEquals(1, result);
        verify(bookDAO, never()).save(any(Book.class));
    }

    @Test
    void save_newBook_savesAndReturnsNewId() {
        BookDTO book = new BookDTO(2, "Brave New World", "Aldous Huxley", 1932,"Science Fiction");
        when(bookDAO.findByTitleAndAuthor("Brave New World", "Aldous Huxley")).thenReturn(Optional.empty());
        when(bookDAO.save(any(Book.class))).thenReturn(2);

        Integer result = bookService.save(book);

        assertEquals(2, result);
        verify(bookDAO).save(argThat(savedBook ->
                savedBook.getTitle().equals("Brave New World") &&
                        savedBook.getAuthor().equals("Aldous Huxley") &&
                        savedBook.getGenre().equals("Science Fiction") &&
                        savedBook.getYear() == 1932
        ));
    }

}
