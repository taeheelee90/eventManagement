package com.taeheelee.eventmanagement.modules.activity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.taeheelee.eventmanagement.modules.event.Event;
import com.taeheelee.eventmanagement.modules.event.EventRepository;
import com.taeheelee.eventmanagement.modules.event.EventService;

@MockMvcForTest
public class EventControllerTest {

	@Autowired
	MockMvc mockMvc;
	@Autowired
	EventService eventService;
	@Autowired
	EventRepository eventRepository;
	@Autowired
	AccountRepository accountRepository;
	@Autowired
	AccountObjectMother accountObjectMother;
	@Autowired
	EventObjectMother eventObjectMother;

	@Test
	@WithAccount("testUser")
	@DisplayName("event form view test")
	void show_createEventForm() throws Exception {
		mockMvc.perform(get("/new-event"))
				.andExpect(status().isOk())
				.andExpect(view().name("event/form"))
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("eventForm"));
	}
	
	@Test
	@WithAccount("testUser")
	@DisplayName("create event - success")
	void createEvent_success() throws Exception {
		mockMvc.perform(post("/new-event")
				.param("path", "test-path")
				.param("title", "Test Title")
				.param("shortDescription", "short description")
				.param("fullDescription", "full description for test event")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/event/test-path"));
				
	}
	
	@Test
	@WithAccount("testUser")
	@DisplayName("create event - fail")
	void createEvent_fail() throws Exception {
		mockMvc.perform(post("/new-event")
				.param("path", "")
				.param("title", "Test Title")
				.param("shortDescription", "short description")
				.param("fullDescription", "full description for test event")
				.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(view().name("event/form"))
				.andExpect(model().hasErrors())
				.andExpect(model().attributeExists("eventForm"))
				.andExpect(model().attributeExists("account"));
		
		Event event = eventRepository.findByPath("wrong path");
		assertNull(event);
	}
	
	
	@Test
	@WithAccount("testUser")
	@DisplayName("event view test")
	void show_event() throws Exception {
		Event event = new Event();
		event.setPath("test-path");
		event.setTitle("Test title");
		event.setShortDescription("short description");
		event.setFullDescription("full description for test event");
		
		Account testUser = accountRepository.findByNickname("testUser");
		eventService.createNewEvent(event, testUser);
		
		mockMvc.perform(get("/event/test-path"))
				.andExpect(view().name("event/view"))
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("event"));
	}
	
	
	@Test
	@WithAccount("testUser")
	@DisplayName("Join event")
	void joinEvent() throws Exception {
		Account manager = accountObjectMother.createAccount("manager");
		Event event = eventObjectMother.createEvent("test-event", manager);
		
		mockMvc.perform(get("/event/" + event.getPath() + "/join"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/event/" + event.getPath() + "/members"));
		
		Account testUser = accountRepository.findByNickname("testUser");
		assertTrue(event.getMembers().contains(testUser));
	}
	
	
	@Test
	@WithAccount("testUser")
	@DisplayName("Leave event")
	void leaveEvent() throws Exception {
		Account manager = accountObjectMother.createAccount("manager");
		Event event = eventObjectMother.createEvent("test-event", manager);
		Account testUser = accountRepository.findByNickname("testUser");
		eventService.addMember(event, testUser);
		
		mockMvc.perform(get("/event/" + event.getPath() + "/leave"))
			   .andExpect(status().is3xxRedirection())
			   .andExpect(redirectedUrl("/event/" + event.getPath() + "/members"));
		
		assertFalse(event.getMembers().contains(testUser));
		
	}
}
