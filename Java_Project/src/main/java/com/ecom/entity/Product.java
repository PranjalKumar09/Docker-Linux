package com.ecom.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Product {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 150)
    private String title;
    @Column(length = 1000)
    private String description;
    private String category;
    private Double price;
    private int stock;
    private String image;

    private int discount;
    private Double discount_price;

    private Boolean isActive;
}
