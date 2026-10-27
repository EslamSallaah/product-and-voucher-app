package com.example.voucherapp.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity 
public class voucher {

    @Id 
@GeneratedValue(strategy = GenerationType.IDENTITY)
private long id;
private String code;
private BigDecimal discount;
private String expire_date;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getDiscount() {
        return this.discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getExpire_date() {
        return this.expire_date;
    }

    public void setExpire_date(String expire_date) {
        this.expire_date = expire_date;
    }





}
