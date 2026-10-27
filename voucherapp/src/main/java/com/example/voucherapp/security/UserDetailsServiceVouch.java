package com.example.voucherapp.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.voucherapp.model.User;
import com.example.voucherapp.repo.UserRepo;

@Service
public class UserDetailsServiceVouch implements UserDetailsService {

final UserRepo userRepo;

    UserDetailsServiceVouch(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("username not found " + username);
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),user.getRoles());
    }
}
