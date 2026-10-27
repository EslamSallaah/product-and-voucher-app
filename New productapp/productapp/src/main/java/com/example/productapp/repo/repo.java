package com.example.productapp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.productapp.model.products;

public interface repo extends JpaRepository<products,Long> {


    
}
