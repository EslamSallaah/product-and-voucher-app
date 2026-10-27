package com.example.voucherapp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.voucherapp.model.voucher;


public interface voucherrepo extends JpaRepository<voucher,Long> {

voucher findByCode(String code);

}
