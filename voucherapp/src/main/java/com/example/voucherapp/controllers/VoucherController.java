package com.example.voucherapp.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.example.voucherapp.model.voucher;
import com.example.voucherapp.repo.voucherrepo;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VoucherController {

    private final voucherrepo repo;

    VoucherController(voucherrepo repo) {
        this.repo = repo;
    }

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        model.addAttribute("isAdmin", isAdmin(authentication));
        return "index";
    }

    @GetMapping("/showCreateVoucher")
    public String showCreateVoucher() {
        return "createVoucher";
    }

    @PostMapping("/saveVoucher")
    public String save(@ModelAttribute voucher voucher) {
        repo.save(voucher);
        return "createResponse";
    }

    @GetMapping("/showGetVoucher")
    public String showGetVoucher(Authentication authentication, Model model) {
        model.addAttribute("isAdmin", isAdmin(authentication));
        return "getVoucher";
    }

    @PostMapping("/getVoucher")
    public String getVoucher(@RequestParam String code, Model model) {
        voucher foundVoucher = repo.findByCode(code.trim());
        if (foundVoucher == null) {
            model.addAttribute("error", "No voucher was found with that code.");
            return "getVoucher";
        }
        model.addAttribute("voucher", foundVoucher);
        return "voucherDetails";
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
