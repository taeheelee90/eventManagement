package com.taeheelee.eventmanagement.account;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import com.taeheelee.eventmanagement.Account.AccountRepository;
import com.taeheelee.eventmanagement.domain.Account;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private AccountRepository accountRepository;
	@MockBean
	JavaMailSender javaMailSender;

	@DisplayName("login view test")
	@Test
	void signUpForm() throws Exception {
		mockMvc.perform(get("/sign-up")).andExpect(status().isOk()).andExpect(view().name("account/sign-up.html"))
				.andExpect(model().attributeExists("signUpForm"));
	}

	@DisplayName("signup handling - invalid input")
	@Test
	void signUpSubmit_with_wrong_input() throws Exception {
		mockMvc.perform(post("/sign-up").param("nickname", "testname1").param("email", "email..")
				.param("password", "12345").with(csrf())).andExpect(status().isOk())
				.andExpect(view().name("account/sign-up.html")).andExpect(unauthenticated());

	}

	@DisplayName("signup handling - valid input")
	@Test
	void signUpSubmit_with_correct_input() throws Exception {
		mockMvc.perform(post("/sign-up").param("nickname", "testname1").param("email", "my@email.com")
				.param("password", "12345678").with(csrf())).andExpect(status().is3xxRedirection()) // Status 302
				.andExpect(view().name("redirect:/"));

		// Check save and PW encode
		Account account = accountRepository.findByEmail("my@email.com");
		assertNotNull(account);
		assertNotEquals(account.getPassword(),"12345678");

		// Check email generate token sent
		then(javaMailSender).should().send(any(SimpleMailMessage.class));
	}

}
