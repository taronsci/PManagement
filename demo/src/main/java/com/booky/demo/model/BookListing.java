package com.booky.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "booklisting")
@Getter
@Setter
public class BookListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "book_id")
    private Integer bookId;

    @Column(name = "owner_id")
    private Integer ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition", nullable = false, columnDefinition = "book_condition")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private BookCondition condition;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, columnDefinition = "transaction_type")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "request_status")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private RequestStatus status;

    @OneToOne(mappedBy = "bookListing", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private Details details;

}
