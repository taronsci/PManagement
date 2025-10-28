package com.booky.demo.dao;

import com.booky.demo.dto.UserDTO;
import com.booky.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer register(User user) {
        String sql = "INSERT INTO users (name, surname, username, email, password) VALUES(?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,user.getName());
            ps.setString(2,user.getSurname());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPassword());
            return ps;
        }, keyHolder);
        Number key = (Number) keyHolder.getKeys().get("id");

        return key.intValue();
    }

    public Optional<Integer> getIdByUsername(String username){
        String sql = "SELECT id FROM users WHERE username = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, Integer.class, username));
        }catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<String> getEmailById(Integer id){
        String sql = "SELECT email FROM users WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, String.class, id));
        }catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Integer> getIdByEmail(String email){
        String sql = "SELECT id FROM users WHERE email = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, Integer.class, email));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public String getPasswordHashById(Integer id){
        String sql = "SELECT password FROM users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Optional<String> getUsernameById(Integer id){
        String sql = "SELECT username FROM users WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, String.class, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public UserDTO updateProfileDetails(User user) {
        if (user.getUsername() != null) {
            Optional<Integer> existingId = getIdByUsername(user.getUsername());
            if (existingId.isPresent() && !existingId.get().equals(user.getId())) {
                throw new IllegalArgumentException("Username already taken");
            }
        }
        if (user.getEmail() != null) {
            Optional<Integer> existingId = getIdByEmail(user.getEmail());
            if (existingId.isPresent() && !existingId.get().equals(user.getId())) {
                throw new IllegalArgumentException("Email already taken");
            }
        }

        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("UPDATE users SET ");

        if (user.getUsername() != null) {
            sql.append("username = ?, ");
            params.add(user.getUsername());
        }
        if (user.getEmail() != null) {
            sql.append("email = ?, ");
            params.add(user.getEmail());
        }
        if (params.isEmpty()) {
            return getUserDTOById(user.getId());
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");
        params.add(user.getId());

        jdbcTemplate.update(sql.toString(), params.toArray());

        return getUserDTOById(user.getId());
    }

    public UserDTO getUserDTOById(Integer id) {
        String sql = "SELECT id, name, surname, username, email FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                        new UserDTO(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("surname"),
                                rs.getString("username"),
                                rs.getString("email"),
                                null
                        ), id
        );
    }
}
