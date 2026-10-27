package com.example.productapp.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "voucher")
public class Voucher {

    @Id
    private long id;

    private String code;
    private BigDecimal discount;

    @Column(name = "expire_date")
    private String expireDate;

    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public String getExpireDate() {
        return expireDate;
    }
}
