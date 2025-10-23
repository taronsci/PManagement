package com.booky.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "rentdetails")
@Getter
@Setter
public class RentDetails {
    @Id
    @Column(name = "listing_id")
    private Integer id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "listing_id")
    private Details details;

    @Column(name = "rental_start_date")
    private LocalDate rentalStartDate; // nullable until rented

    @Column(name = "rental_duration", nullable = false)
    private Integer rentalDuration;
}
