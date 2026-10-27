package com.example.voucherapp.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.voucherapp.model.voucher;
import com.example.voucherapp.repo.voucherrepo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController 
@RequestMapping("/voucherapi/vouchers")
public class VoucherRestController {

final voucherrepo repo;

  VoucherRestController(voucherrepo repo) {
    this.repo = repo;
  }

@PostMapping
public voucher create(@RequestBody voucher Voucher) {
    
    return repo.save(Voucher);
}

@GetMapping("/{code}")
public voucher getVoucher(@PathVariable("code") String code) {
    return repo.findByCode(code);
}


}
