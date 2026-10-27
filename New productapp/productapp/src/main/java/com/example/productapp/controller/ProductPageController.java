package com.example.productapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.productapp.Dto.ProductCreateRequest;
import com.example.productapp.Dto.ProductOffer;
import com.example.productapp.repo.repo;
import com.example.productapp.service.ProductService;

import java.math.BigDecimal;

@Controller
public class ProductPageController {

    private final repo productRepository;
    private final ProductService productService;

    public ProductPageController(repo productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/products";
    }

    @GetMapping("/products")
    public String products(Model model, Authentication authentication) {
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("isAdmin", isAdmin(authentication));
        model.addAttribute("username", authentication.getName());
        return "products";
    }

    @GetMapping("/products/new")
    public String newProduct() {
        return "createProduct";
    }

    @PostMapping("/products")
    public String createProduct(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String price,
            RedirectAttributes redirectAttributes) {
        try {
            productService.create(new ProductCreateRequest(
                name,
                description,
                new BigDecimal(price)));
            redirectAttributes.addFlashAttribute("success", "Product added.");
            return "redirect:/products";
        } catch (NumberFormatException exception) {
            return redirectWithProductError(redirectAttributes, "Enter a valid product price.");
        } catch (ResponseStatusException exception) {
            return redirectWithProductError(redirectAttributes, exception.getReason());
        }
    }

    @PostMapping("/products/{id}/apply-voucher")
    public String applyVoucher(
            @PathVariable long id,
            @RequestParam String voucherCode,
            RedirectAttributes redirectAttributes) {
        try {
            ProductOffer offer = productService.applyVoucher(id, voucherCode);
            redirectAttributes.addFlashAttribute("offer", offer);
        } catch (ResponseStatusException exception) {
            redirectAttributes.addFlashAttribute("voucherError", exception.getReason());
        }
        return "redirect:/products";
    }

    private String redirectWithProductError(RedirectAttributes attributes, String error) {
        attributes.addFlashAttribute("productError", error);
        return "redirect:/products/new";
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch("ROLE_ADMIN"::equals);
    }
}
