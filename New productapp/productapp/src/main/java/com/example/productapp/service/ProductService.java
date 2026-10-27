package com.example.productapp.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.productapp.Dto.ProductCreateRequest;
import com.example.productapp.Dto.ProductOffer;
import com.example.productapp.model.Voucher;
import com.example.productapp.model.products;
import com.example.productapp.repo.VoucherRepository;
import com.example.productapp.repo.repo;

@Service
public class ProductService {

    private final repo productRepository;
    private final VoucherRepository voucherRepository;

    public ProductService(repo productRepository, VoucherRepository voucherRepository) {
        this.productRepository = productRepository;
        this.voucherRepository = voucherRepository;
    }

    public products create(ProductCreateRequest request) {
        if (request == null
                || request.name() == null || request.name().isBlank()
                || request.description() == null || request.description().isBlank()
                || request.price() == null || request.price().signum() <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Name, description, and a price greater than zero are required");
        }

        products product = new products();
        product.setName(request.name().trim());
        product.setDescription(request.description().trim());
        product.setPrice(request.price());
        return productRepository.save(product);
    }

    public ProductOffer applyVoucher(long productId, String voucherCode) {
        products product = productRepository.findById(productId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        if (voucherCode == null || voucherCode.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voucher code is required");
        }

        Voucher voucher = voucherRepository.findFirstByCodeIgnoreCase(voucherCode.trim())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voucher not found"));
        validateVoucherExpiry(voucher);
        if (product.getPrice() == null || product.getPrice().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product price is invalid");
        }
        if (voucher.getDiscount() == null || voucher.getDiscount().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voucher discount is invalid");
        }

        BigDecimal discountedPrice = product.getPrice().subtract(voucher.getDiscount()).max(BigDecimal.ZERO);
        return new ProductOffer(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            voucher.getCode(),
            voucher.getDiscount(),
            discountedPrice);
    }

    private void validateVoucherExpiry(Voucher voucher) {
        if (voucher.getExpireDate() == null || voucher.getExpireDate().isBlank()) {
            return;
        }
        try {
            if (LocalDate.parse(voucher.getExpireDate()).isBefore(LocalDate.now())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voucher has expired");
            }
        } catch (DateTimeParseException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voucher expiration date is invalid", exception);
        }
    }
}
