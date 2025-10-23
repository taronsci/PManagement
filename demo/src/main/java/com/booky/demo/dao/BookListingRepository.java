package com.booky.demo.dao;

import com.booky.demo.model.BookListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface BookListingRepository extends JpaRepository<BookListing,Integer> {

    @Query("SELECT b FROM BookListing b " +
            "LEFT JOIN FETCH b.details d " +
            "LEFT JOIN FETCH d.rentDetails")
    Page<BookListing> findAllWithDetails(Pageable pageable);

    @Query("SELECT b FROM BookListing b " +
            "LEFT JOIN FETCH b.details d " +
            "LEFT JOIN FETCH d.rentDetails " +
            "WHERE b.ownerId = :ownerId")
    Page<BookListing> findByIdWithDetails(@Param("ownerId") int ownerId, Pageable pageable);

    void deleteById(@Param("id") int id);

    @Query(
            value = """
        SELECT b.id AS book_id,
               b.title,
               b.author,
               b.year,
               b.genre,
               bl.id,
               bl.owner_id,
               bl.condition,
               bl.transaction_type,
               bl.status
        FROM booklisting bl
        LEFT JOIN book b ON bl.book_id = b.id
        LEFT JOIN details d ON d.listing_id = bl.id
        LEFT JOIN rentdetails rd ON rd.listing_id = d.listing_id
        WHERE (:query IS NULL OR b.title ILIKE CONCAT('%', :query, '%')
               OR b.author ILIKE CONCAT('%', :query, '%'))
          AND (:filter IS NULL OR bl.transaction_type = CAST(:filter AS transaction_type))
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM booklisting bl
        LEFT JOIN book b ON bl.book_id = b.id
        LEFT JOIN details d ON d.listing_id = bl.id
        LEFT JOIN rentdetails rd ON rd.listing_id = d.listing_id
        WHERE (:query IS NULL OR b.title ILIKE CONCAT('%', :query, '%')
               OR b.author ILIKE CONCAT('%', :query, '%'))
          AND (:filter IS NULL OR bl.transaction_type = CAST(:filter AS transaction_type))
        """,
            nativeQuery = true
    )
    Page<BookListing> searchBooks(@Param("query") String query,
                                  @Param("filter") String filter,
                                  Pageable pageable);

}
