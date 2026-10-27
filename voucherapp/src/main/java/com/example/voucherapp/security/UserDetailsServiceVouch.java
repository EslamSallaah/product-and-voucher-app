package com.example.voucherapp.security;

import java.util.Locale;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
        User user = userRepo.findByEmailIgnoreCase(username);
        if (user == null) {
            throw new UsernameNotFoundException("username not found " + username);
        }
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            user.getRoles().stream()
                .map(role -> normalizeRole(role.getAuthority()))
                .map(SimpleGrantedAuthority::new)
                .toList());
    }

    private String normalizeRole(String role) {
        String normalizedRole = role.trim().toUpperCase(Locale.ROOT);
        return normalizedRole.startsWith("ROLE_") ? normalizedRole : "ROLE_" + normalizedRole;
    }
}
