package com.taeheelee.eventmanagement.modules.account;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.taeheelee.eventmanagement.infra.MockMvcForTest;
import com.taeheelee.eventmanagement.infra.mail.EmailMessage;
import com.taeheelee.eventmanagement.infra.mail.EmailService;

@MockMvcForTest
public class AccountControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private AccountRepository accountRepository;
	@MockBean
	EmailService emailService;
	
	@DisplayName("email confirmation - fail")
	@Test
	void checkEmailToken_fail() throws Exception{
		
	    mockMvc.perform(get("/check-email-token")
                .param("token", "sdfjslwfwef")
                .param("email", "email@email.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated());;
				
     }
	
	@DisplayName("email confirmation - success")
	@Test
	void checkEmailToken_success() throws Exception{
		
		Account account = Account.builder()
				.email("test@email.com")
				.password("12345678")
				.nickname("testname")
				.build();
				
		
		Account newAccount = accountRepository.save(account);
		newAccount.generateEmailCheckToken();
		
	    mockMvc.perform(get("/check-email-token")
                .param("token", newAccount.getEmailCheckToken())
                .param("email", newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated().withUsername("testname"));;
                
				
     }

	@DisplayName("login view test")
	@Test
	void show_signupForm() throws Exception {
		mockMvc.perform(get("/sign-up"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("account/sign-up.html"))
				.andExpect(model().attributeExists("signUpForm"));
	}


	@DisplayName("signup handling - fail")
	@Test
	void signUpSubmit_fail() throws Exception {
		mockMvc.perform(post("/sign-up")
				.param("nickname", "testname1")
				.param("email", "email..")
				.param("password", "12345")
				.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(view().name("account/sign-up.html"))
				.andExpect(unauthenticated());

	}

	@DisplayName("signup handling - success")
	@Test
	void signUpSubmit_success() throws Exception {
		mockMvc.perform(post("/sign-up")
				.param("nickname", "testname1")
				.param("email", "my@email.com")
				.param("password", "12345678")
				.with(csrf()))
				.andExpect(status().is3xxRedirection()) // Status 302
				.andExpect(view().name("redirect:/"))
				.andExpect(authenticated().withUsername("testname1"));;

		// Check save and PW encode
		Account account = accountRepository.findByEmail("my@email.com");
		assertNotNull(account);
		assertNotEquals(account.getPassword(),"12345678");
		assertNotNull(account.getEmailCheckToken());

		// Check email generate token sent
		then(emailService).should().sendEmail(any(EmailMessage.class));
	}
	

}
