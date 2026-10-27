package com.example.voucherapp.controllers;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.voucherapp.model.Role;
import com.example.voucherapp.model.User;
import com.example.voucherapp.repo.RoleRepo;
import com.example.voucherapp.repo.UserRepo;

@Controller
public class RegistrationController {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    RegistrationController(UserRepo userRepo, RoleRepo roleRepo, BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String name,
            @RequestParam String lastname,
            @RequestParam String email,
            @RequestParam String password) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if (name.isBlank() || lastname.isBlank()
                || !EMAIL_PATTERN.matcher(normalizedEmail).matches()
                || password.length() < 8) {
            return "redirect:/register.html?error=invalid";
        }
        if (userRepo.existsByEmailIgnoreCase(normalizedEmail)) {
            return "redirect:/register.html?error=exists";
        }

        Role userRole = roleRepo.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole = roleRepo.save(userRole);
        }

        User user = new User();
        user.setName(name.trim());
        user.setLastname(lastname.trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Set.of(userRole));
        userRepo.save(user);

        return "redirect:/login.html?registered=true";
    }
}
