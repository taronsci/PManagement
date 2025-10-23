//package com.booky.demo.dao;
//
////import com.booky.demo.model.RequestStatus;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import org.springframework.stereotype.Component;
//
//@Component
//public class BookListingDAO {
//
//    private final JdbcTemplate jdbcTemplate;
//
//    @Autowired
//    public BookListingDAO(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
////    public Integer save(BookListing listing) {
////        System.out.println("Saving listing");
////
////        String sql = "INSERT INTO booklisting(book_id, owner_id, condition, transaction_type, status) VALUES(?, ?, ?, ?, ?)";
////        KeyHolder keyHolder = new GeneratedKeyHolder();
////
////        jdbcTemplate.update(connection -> {
////                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
////                    ps.setInt(1, listing.getBook_id());
////                    ps.setInt(2, listing.getOwner_id());
////                    ps.setString(3, listing.getCondition().toString());
////                    ps.setString(4, listing.getTransaction_type().toString());
////                    ps.setString(5, listing.getStatus().toString());
////                    return ps;
////                }, keyHolder);
////
////        Number key = (Number) keyHolder.getKeys().get("id");
////
////        return key.intValue();
////    }
//}
