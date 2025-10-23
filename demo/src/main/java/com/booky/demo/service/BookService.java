package com.booky.demo.service;

import com.booky.demo.dao.BookDAO;
import com.booky.demo.dto.BookDTO;
import com.booky.demo.model.Book;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookService {
    private final BookDAO bookDAO;

    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    @Transactional
    public Integer save(BookDTO book) {
        Optional<Integer> id = bookDAO.findByTitleAndAuthor(book.title(), book.author());

        Book newBook = new Book();
        newBook.setAuthor(book.author());
        newBook.setTitle(book.title());
        newBook.setGenre(book.genre());
        newBook.setYear(book.year());

        return id.orElseGet(() -> bookDAO.save(newBook));
    }
}
