package com.booky.demo.dao;

import com.booky.demo.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Component
public class BookDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BookDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer save(Book book) {
        String sql = "INSERT INTO book(title, author, year, genre) VALUES(?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setInt(3, book.getYear());
            ps.setString(4, book.getGenre());
            return ps;
        }, keyHolder);
        Number key = (Number) keyHolder.getKeys().get("id");

        return key.intValue();
    }

    public boolean findById(Integer bookId){
        String sql = "SELECT COUNT(*) FROM book WHERE id = ?";
        Optional<Integer> count = Optional.ofNullable(jdbcTemplate.queryForObject(sql, Integer.class, bookId));
        return count.isPresent() && count.get() > 0;
    }

    public Optional<Integer> findByTitleAndAuthor(String title, String author){
        String sql = "SELECT id FROM book WHERE title = ? AND author = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, Integer.class, title, author));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    public Optional<Book> findBookById(Integer bookId){
        String sql = "SELECT * FROM book WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Book.class), bookId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

}
