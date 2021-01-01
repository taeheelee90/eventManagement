package com.taeheelee.eventmanagement.modules.event;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.taeheelee.eventmanagement.infra.MockMvcForTest;
import com.taeheelee.eventmanagement.modules.account.Account;
import com.taeheelee.eventmanagement.modules.account.AccountObjectMother;
import com.taeheelee.eventmanagement.modules.account.AccountRepository;
import com.taeheelee.eventmanagement.modules.account.WithAccount;

@MockMvcForTest
public class EventSettingsControllerTest {
	
	@Autowired MockMvc mockMvc;
	@Autowired EventObjectMother eventObjectMother;
	@Autowired AccountObjectMother accountObjectMother;
	@Autowired AccountRepository accountRepository;
	@Autowired EventRepository eventRepository;

	@Test
	@WithAccount("testUser")
	@DisplayName("Description edit view test - fail")
	void updateDescriptionForm_fail() throws Exception {
		Account manager = accountObjectMother.createAccount("manager");
		Event event = eventObjectMother.createEvent("test-event", manager);
		
		mockMvc.perform(get("/event/" + event.getPath() + "/settings/description"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithAccount("testUser")
	@DisplayName("Description edit view test - success")
	void updateDescriptionForm_success() throws Exception {
		Account testUser = accountRepository.findByNickname("testUser");
		Event event = eventObjectMother.createEvent("test-event", testUser);

		mockMvc.perform(get("/event/" + event.getPath() + "/settings/description"))
				.andExpect(view().name("event/settings/description"))
				.andExpect(model().attributeExists("eventDescriptionForm"))
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("event"));
	}
	
	@Test
	@WithAccount("testUser")
	@DisplayName("Edit description - success")
	void editDescription_success() throws Exception {
		Account testUser = accountRepository.findByNickname("testUser");
		Event event = eventObjectMother.createEvent("test-event", testUser);
		
		String url = "/event/" + event.getPath() + "/settings/description";
		mockMvc.perform(post(url)
				.param("shortDescription", "short description")
				.param("fullDescription", "full description for test event")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(url))
				.andExpect(flash().attributeExists("message"));
		
	}
	
	@Test
	@WithAccount("testUser")
	@DisplayName("Edit description - fail")
	void editDescription_fail() throws Exception {
		Account testUser = accountRepository.findByNickname("testUser");
		Event event = eventObjectMother.createEvent("test-path", testUser);
		
		String url = "/event/" + event.getPath() + "/settings/description";
		mockMvc.perform(post(url)
				.param("shortDescription", "")
				.param("fullDescription", "full description for test event")
				.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(model().hasErrors())
				.andExpect(model().attributeExists("eventDescriptionForm"))
				.andExpect(model().attributeExists("event"))
				.andExpect(model().attributeExists("account"));
	}
}
