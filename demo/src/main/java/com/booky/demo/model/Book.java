package com.booky.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "book")
@Getter
@Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column
    private Integer year;

    @Column
    private String genre;

    public String toString(){
        StringBuilder a = new StringBuilder();
        a.append(title);
        a.append(" by ");
        a.append(author).append("\n");
        if(year != null)
            a.append("year: ").append(year).append("\n");
        if(genre != null)
            a.append("genre: ").append(genre);

        return a.toString();
    }

}
