package com.finology.crawler.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column
    private Long id;

    @Column
    private String title;

    @Column
    private String price;

    @Column
    private String description;

    @Column
    private String extraInfo;
}
