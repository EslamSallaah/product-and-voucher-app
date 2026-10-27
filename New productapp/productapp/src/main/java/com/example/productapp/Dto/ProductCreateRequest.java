package com.example.productapp.Dto;

import java.math.BigDecimal;

public record ProductCreateRequest(String name, String description, BigDecimal price) {
}
