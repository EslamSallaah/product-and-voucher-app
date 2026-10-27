package com.example.productapp.Dto;

import java.math.BigDecimal;

public record ProductOffer(
        long productId,
        String productName,
        String description,
        BigDecimal originalPrice,
        String voucherCode,
        BigDecimal discount,
        BigDecimal discountedPrice) {
}
