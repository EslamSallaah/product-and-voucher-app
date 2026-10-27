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
        http.formLogin(formLogin -> formLogin.disable());
        http.httpBasic(httpBasic -> {});
        http.authorizeHttpRequests(authorize -> authorize
            .requestMatchers(HttpMethod.GET, "/voucherapi/vouchers/**","/","/showGetVoucher","/getVoucher")
            .hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.GET, "/showCreateVoucher", "/createVoucher","/createResponse")
            .hasAnyRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/voucherapi/vouchers","/saveVoucher")
            .hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/getVoucher")
            .hasAnyRole("USER", "ADMIN"));
        http.csrf(csrf -> csrf.disable());  
        return http.build();

    }

}
