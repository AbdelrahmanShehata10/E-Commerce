package com.example.demo.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", columnDefinition = "TEXT", length = 255)
    private String name;

    @Column(name = "category", columnDefinition = "TEXT", length = 100)
    private String category;

    @Column(name = "description", columnDefinition = "TEXT", length = 500)
    private String description;

    @Column(name = "image_url", columnDefinition = "TEXT", length = 500)
    private String imageUrl;

    @Column(name = "price")
    private Double  price;

    @Column(name = "on_sale")
    private Boolean onSale;

    @Column(name = "discount")
    private Double  discount;

    @Column(name = "FinalPrice")
    private Double  FinalPrice;
    @Column(name = "stock")
    private Integer stock;


    @PrePersist
    @PreUpdate
    public void calculateFinalPrice() {
        if (discount > 0) {
            this.onSale = true;
            this.FinalPrice = price - (price * discount / 100);
        } else {
            this.onSale = false;
            this.FinalPrice = price;
        }
    }
}
