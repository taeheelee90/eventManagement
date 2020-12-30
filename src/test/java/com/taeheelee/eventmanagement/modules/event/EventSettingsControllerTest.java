package com.taeheelee.eventmanagement.modules.event;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.taeheelee.eventmanagement.modules.account.Account;
import com.taeheelee.eventmanagement.modules.account.WithAccount;
import com.taeheelee.eventmanagement.modules.event.Event;

import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
public class EventSettingsControllerTest extends EventControllerTest {

	@Test
	@WithAccount("testUser")
	@DisplayName("Show description edit form - Fail")
	void updateDescriptionForm_fail() throws Exception {
		Account account = createAccount("account");
		Event event = createEvent("test-event", account);

		mockMvc.perform(get("/event/" + event.getPath() + "/settings/description")).andExpect(status().isForbidden());
	}

	@Test
	@WithAccount("testUser")
	@DisplayName("Show description edit form - success")
	void updateDescriptionForm_success() throws Exception {
		Account manager = accountRepository.findByNickname("testUser");
		Event event = createEvent("test-event", manager);

		mockMvc.perform(get("/event/" + event.getPath() + "/settings/description")).andExpect(status().isOk())
				.andExpect(view().name("event/settings/description"))
				.andExpect(model().attributeExists("eventDescriptionForm"))
				.andExpect(model().attributeExists("account")).andExpect(model().attributeExists("event"));
	}

	@Test
	@WithAccount("testUser")
	@DisplayName("Update description - success")
	void upadateDescription_success() throws Exception {
		Account manager = accountRepository.findByNickname("testUser");
		Event event = createEvent("test-event", manager);

		String url = "/event/" + event.getPath() + "/settings/description";
		mockMvc.perform(post(url).param("shortDescription", "short description")
				.param("fullDescription", "full description").with(csrf())).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(url)).andExpect(flash().attributeExists("message"));

	}

	@Test
	@WithAccount("testUser")
	@DisplayName("Update description - fail")
	void upadateDescription_fail() throws Exception {
		Account manager = accountRepository.findByNickname("testUser");
		Event event = createEvent("test-event", manager);

		String url = "/event/" + event.getPath() + "/settings/description";
		mockMvc.perform(
				post(url).param("shortDescription", "").param("fullDescription", "full description").with(csrf()))
				.andExpect(status().isOk()).andExpect(model().hasErrors())
				.andExpect(model().attributeExists("eventDescriptionForm")).andExpect(model().attributeExists("event"))
				.andExpect(model().attributeExists("account"));
	}

}
