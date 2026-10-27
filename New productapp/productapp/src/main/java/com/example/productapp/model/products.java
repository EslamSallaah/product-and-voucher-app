package com.example.productapp.model;

import jakarta.persistence.GeneratedValue;
import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity 
public class products {

    @Id 
@GeneratedValue(strategy = GenerationType.IDENTITY)
private long id;
private String description;
private String name;
private BigDecimal price;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


}
