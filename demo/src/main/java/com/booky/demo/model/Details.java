package com.booky.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "details")
@Getter
@Setter
public class Details {
    @Id
    @Column(name = "listing_id")
    private Integer id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "listing_id")
    private BookListing bookListing;

    @Column(nullable = false)
    private Double price;

    @OneToOne(mappedBy = "details", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private RentDetails rentDetails;
}
