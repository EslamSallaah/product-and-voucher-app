package com.example.productapp;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.productapp.model.products;
import com.example.productapp.repo.VoucherRepository;
import com.example.productapp.repo.repo;
import com.example.productapp.security.DatabaseUserDetailsService;

@SpringBootTest
@AutoConfigureMockMvc
class ProductappApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private repo productRepository;

	@Autowired
	private VoucherRepository voucherRepository;

	@Autowired
	private DatabaseUserDetailsService userDetailsService;

	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	void signedInUserCanViewProductsAndLoginPageRenders() throws Exception {
		mockMvc.perform(get("/login"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Sign in with the email")));

		products product = new products();
		product.setName("Test product");
		product.setDescription("Temporary test product");
		product.setPrice(new BigDecimal("12.50"));
		productRepository.save(product);

		mockMvc.perform(get("/products").with(user("member@example.com").roles("USER")))
			.andExpect(status().isOk())
			.andExpect(view().name("products"))
			.andExpect(content().string(containsString("Test product")));
	}

	@Test
	void onlyAdminsCanOpenProductCreationPage() throws Exception {
		mockMvc.perform(get("/products/new").with(user("member@example.com").roles("USER")))
			.andExpect(status().isForbidden());

		mockMvc.perform(get("/products/new").with(user("admin@example.com").roles("ADMIN")))
			.andExpect(status().isOk())
			.andExpect(view().name("createProduct"));
	}

	@Test
	void regularUserCannotCreateProductsThroughTheApi() throws Exception {
		mockMvc.perform(post("/productapi/products")
				.with(user("member@example.com").roles("USER"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"name":"Not allowed","description":"Must be admin","price":10.00}
					"""))
			.andExpect(status().isForbidden());
	}

	@Test
	void accountLookupQueriesTheSharedUserRoleTables() {
		assertThrows(
			UsernameNotFoundException.class,
			() -> userDetailsService.loadUserByUsername("not-a-real-account@example.invalid"));
	}

	@Test
	void voucherLookupUsesTheSharedVoucherTable() {
		assertTrue(voucherRepository.findFirstByCodeIgnoreCase(UUID.randomUUID().toString()).isEmpty());
	}
}
