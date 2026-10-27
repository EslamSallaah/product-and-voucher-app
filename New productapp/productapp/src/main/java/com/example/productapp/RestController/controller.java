package com.example.productapp.RestController;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.productapp.repo.repo;
import com.example.productapp.Dto.ApplyVoucherRequest;
import com.example.productapp.Dto.ProductCreateRequest;
import com.example.productapp.Dto.ProductOffer;
import com.example.productapp.model.products;
import com.example.productapp.service.ProductService;


@RestController
@RequestMapping("/productapi")
public class controller {

    private final repo productrepo;
    private final ProductService productService;

    controller(repo productrepo, ProductService productService) {
        this.productrepo = productrepo;
        this.productService = productService;
    }

    @GetMapping("/products")
    public List<products> list() {
        return productrepo.findAll();
    }

    @PostMapping("/products")
    public products create(@RequestBody ProductCreateRequest request) {
        return productService.create(request);
    }

    @PostMapping("/products/{id}/apply-voucher")
    public ProductOffer applyVoucher(
            @PathVariable long id,
            @RequestBody ApplyVoucherRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Voucher code is required");
        }
        return productService.applyVoucher(id, request.voucherCode());
    }
}
