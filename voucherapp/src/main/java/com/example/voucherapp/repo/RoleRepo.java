package com.example.voucherapp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.voucherapp.model.Role;

public interface RoleRepo extends JpaRepository<Role,Long> {
}
