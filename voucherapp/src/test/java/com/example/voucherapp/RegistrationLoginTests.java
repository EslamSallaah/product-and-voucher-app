package com.example.voucherapp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;

import com.example.voucherapp.model.User;
import com.example.voucherapp.model.Role;
import com.example.voucherapp.model.voucher;
import com.example.voucherapp.repo.RoleRepo;
import com.example.voucherapp.repo.UserRepo;
import com.example.voucherapp.repo.voucherrepo;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RegistrationLoginTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private voucherrepo voucherRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void newlyRegisteredUserCanLookUpButCannotCreateVouchers() throws Exception {
        mockMvc.perform(get("/css/app.css"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("text/css"));

        String email = "registration-" + UUID.randomUUID() + "@example.com";
        String password = "test-password-123";

        mockMvc.perform(post("/register")
                .param("name", "Test")
                .param("lastname", "User")
                .param("email", email)
                .param("password", password))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login.html?registered=true"));

        User user = userRepo.findByEmailIgnoreCase(email);
        org.junit.jupiter.api.Assertions.assertNotNull(user);
        org.junit.jupiter.api.Assertions.assertNotEquals(password, user.getPassword());
        org.junit.jupiter.api.Assertions.assertTrue(passwordEncoder.matches(password, user.getPassword()));

        MockHttpSession session = (MockHttpSession) mockMvc.perform(post("/login")
                .param("username", email)
                .param("password", password))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/showGetVoucher"))
            .andReturn()
            .getRequest()
            .getSession(false);

        String dashboard = mockMvc.perform(get("/").session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andReturn()
            .getResponse()
            .getContentAsString();
        org.junit.jupiter.api.Assertions.assertFalse(dashboard.contains("Create a voucher"), dashboard);

        mockMvc.perform(get("/showGetVoucher").session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("getVoucher"));
        mockMvc.perform(get("/showCreateVoucher").session(session))
            .andExpect(status().isForbidden());
        String createdVoucherCode = "user-created-" + UUID.randomUUID();
        mockMvc.perform(post("/saveVoucher").session(session)
                .param("code", createdVoucherCode)
                .param("discount", "1.00")
                .param("expire_date", "2099-12-31"))
            .andExpect(status().isForbidden());

        String voucherCode = "test-" + UUID.randomUUID();
        voucher voucher = new voucher();
        voucher.setCode(voucherCode);
        voucher.setDiscount(java.math.BigDecimal.ONE);
        voucher.setExpire_date("2099-12-31");
        voucherRepo.save(voucher);

        mockMvc.perform(post("/getVoucher").session(session).param("code", voucherCode))
            .andExpect(status().isOk())
            .andExpect(view().name("voucherDetails"));

        mockMvc.perform(get("/voucherapi/vouchers/" + voucherCode).session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(voucherCode));

        mockMvc.perform(post("/voucherapi/vouchers").session(session)
                .contentType("application/json")
                .content("""
                    {"code":"api-%s","discount":2.50,"expire_date":"2099-12-31"}
                    """.formatted(UUID.randomUUID())))
            .andExpect(status().isForbidden());

        mockMvc.perform(post("/getVoucher").session(session).param("code", "unknown-" + UUID.randomUUID()))
            .andExpect(status().isOk())
            .andExpect(view().name("getVoucher"))
            .andExpect(model().attributeExists("error"));

        mockMvc.perform(get("/voucherapi/vouchers/unknown-" + UUID.randomUUID()).session(session))
            .andExpect(status().isNotFound());
    }

    @Test
    void browserLoginAcceptsLegacyAdminRoleWithoutPrefix() throws Exception {
        String email = "legacy-admin-" + UUID.randomUUID() + "@example.com";
        String password = "secure-test-password-123";
        Role legacyAdminRole = new Role();
        legacyAdminRole.setName("ADMIN");
        legacyAdminRole = roleRepo.save(legacyAdminRole);

        User legacyAdmin = new User();
        legacyAdmin.setName("Legacy");
        legacyAdmin.setLastname("Admin");
        legacyAdmin.setEmail(email);
        legacyAdmin.setPassword(passwordEncoder.encode(password));
        legacyAdmin.setRoles(java.util.Set.of(legacyAdminRole));
        userRepo.save(legacyAdmin);

        MockHttpSession session = (MockHttpSession) mockMvc.perform(post("/login")
                .param("username", email)
                .param("password", password))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/showGetVoucher"))
            .andReturn()
            .getRequest()
            .getSession(false);

        mockMvc.perform(get("/showGetVoucher").session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("getVoucher"));
        String dashboard = mockMvc.perform(get("/").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        org.junit.jupiter.api.Assertions.assertTrue(dashboard.contains("Create a voucher"), dashboard);
        mockMvc.perform(get("/showCreateVoucher").session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("createVoucher"));
        mockMvc.perform(post("/saveVoucher").session(session)
                .param("code", "admin-create-" + UUID.randomUUID())
                .param("discount", "1.00")
                .param("expire_date", "2099-12-31"))
            .andExpect(status().isOk())
            .andExpect(view().name("createResponse"));
        mockMvc.perform(post("/voucherapi/vouchers").session(session)
                .contentType("application/json")
                .content("""
                    {"code":"admin-api-%s","discount":2.50,"expire_date":"2099-12-31"}
                    """.formatted(UUID.randomUUID())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void authenticatedAccountWithoutRoleCannotCreateVouchers() throws Exception {
        String email = "no-role-" + UUID.randomUUID() + "@example.com";
        String password = "secure-test-password-123";
        User user = new User();
        user.setName("Test");
        user.setLastname("User");
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(java.util.Set.of());
        userRepo.save(user);

        MockHttpSession session = (MockHttpSession) mockMvc.perform(post("/login")
                .param("username", email)
                .param("password", password))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/showGetVoucher"))
            .andReturn()
            .getRequest()
            .getSession(false);

        mockMvc.perform(get("/showGetVoucher").session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("getVoucher"));
        String dashboard = mockMvc.perform(get("/").session(session))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        org.junit.jupiter.api.Assertions.assertFalse(dashboard.contains("Create a voucher"), dashboard);
        mockMvc.perform(get("/showCreateVoucher").session(session))
            .andExpect(status().isForbidden());
        mockMvc.perform(post("/saveVoucher").session(session)
                .param("code", "no-role-" + UUID.randomUUID())
                .param("discount", "1.00")
                .param("expire_date", "2099-12-31"))
            .andExpect(status().isForbidden());
    }
}
