package com.example.productapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/products", true)
                .failureUrl("/login?error=true")
                .permitAll())
            .httpBasic(Customizer.withDefaults())
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout=true"))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login", "/css/**", "/favicon.ico", "/favicon.svg").permitAll()
                .requestMatchers(HttpMethod.POST, "/productapi/products").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/products/new").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/products").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/products/*/apply-voucher").authenticated()
                .requestMatchers("/productapi/**", "/products/**").authenticated()
                .anyRequest().authenticated())
            .csrf(csrf -> csrf.ignoringRequestMatchers("/productapi/**"));
        return http.build();
    }
}
