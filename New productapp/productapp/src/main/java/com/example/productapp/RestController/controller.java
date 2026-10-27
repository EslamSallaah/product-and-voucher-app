package com.example.productapp.RestController;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.productapp.repo.repo;
import com.example.productapp.Dto.Voucher;
import com.example.productapp.model.products;



@RestController 
@RequestMapping("/productapi")
public class controller {

    private final repo productrepo;

private final RestTemplate restTemplate;

@Value("${voucher-service.url}")
private String voucherServiceURL;

    controller(repo productrepo, RestTemplate restTemplate) {
        this.productrepo = productrepo;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/products")
    public products create(@RequestBody products products) {
        Voucher voucher= restTemplate.getForObject(voucherServiceURL +products.getVouchercode(),Voucher.class);
        products.setPrice(products.getPrice().subtract(voucher.getDiscount()));
        
        return productrepo.save(products);
    }
    



}
