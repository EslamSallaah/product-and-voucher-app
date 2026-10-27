package com.example.voucherapp.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration 
public class WebSecurityConfig {

    @Bean 
    BCryptPasswordEncoder bcryptedBCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean 
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.formLogin(formLogin -> formLogin
            .loginPage("/login.html")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/showGetVoucher", true)
            .failureUrl("/login.html?error=true")
            .permitAll());
        http.httpBasic(httpBasic -> {});
        http.authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/login.html", "/register.html", "/register", "/css/**", "/favicon.ico", "/favicon.svg").permitAll()
            .requestMatchers(HttpMethod.GET, "/showCreateVoucher").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/saveVoucher", "/voucherapi/vouchers").hasRole("ADMIN")
            .anyRequest().authenticated());
        http.csrf(csrf -> csrf.disable());  
        return http.build();

    }

}
