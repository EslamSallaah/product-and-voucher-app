package com.example.voucherapp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.voucherapp.model.User;

public interface UserRepo extends JpaRepository<User,Long> {

    User findByEmail(String email);
}
