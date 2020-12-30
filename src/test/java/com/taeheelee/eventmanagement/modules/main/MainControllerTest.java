package com.taeheelee.eventmanagement.modules.main;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.taeheelee.eventmanagement.modules.account.AccountRepository;
import com.taeheelee.eventmanagement.modules.account.AccountService;
import com.taeheelee.eventmanagement.modules.account.form.SignUpForm;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {
	
	@Autowired MockMvc mockMvc;
	@Autowired AccountService accountService;
	@Autowired AccountRepository accountRepository;
		
	@BeforeEach
	void beforeEach() {
		SignUpForm signUpForm = new SignUpForm();
		signUpForm.setNickname("testuser");
		signUpForm.setEmail("test@email.com");
		signUpForm.setPassword("12345678");
		accountService.processNewAccount(signUpForm);
	}
	
	@AfterEach
	void afterEach() {
		accountRepository.deleteAll();
	}
	
	
	
	@DisplayName("Email Login Success")
	@Test
	void login_with_email() throws Exception {		
				
		mockMvc.perform(post("/login")
				.param("username", "test@email.com")
				.param("password", "12345678")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"))
				.andExpect(authenticated().withUsername("testuser"));;

	}
	
	
	@DisplayName("Nickname Login Success")
	@Test
	void login_with_nickname() throws Exception {
		mockMvc.perform(post("/login")
				.param("username", "testuser")
				.param("password", "12345678")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"))
				.andExpect(authenticated().withUsername("testuser"));;

	}
	
	@DisplayName("Login Fail")
	@Test
	void login_fail() throws Exception {
		mockMvc.perform(post("/login")
				.param("username", "wronguser")
				.param("password", "1111111")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(unauthenticated());;

	}
	
	@WithMockUser	
	@DisplayName("Logout")
	@Test
	void logout() throws Exception {
		mockMvc.perform(post("/logout")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"))
				.andExpect(unauthenticated());;

	}
}
