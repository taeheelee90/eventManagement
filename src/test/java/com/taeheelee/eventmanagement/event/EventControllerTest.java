package com.taeheelee.eventmanagement.event;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.taeheelee.eventmanagement.WithAccount;
import com.taeheelee.eventmanagement.Account.AccountRepository;
import com.taeheelee.eventmanagement.domain.Account;
import com.taeheelee.eventmanagement.domain.Event;

import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
public class EventControllerTest {

	@Autowired MockMvc mockMvc;
	@Autowired EventService eventService;
	@Autowired EventRepository eventRepository;
	@Autowired AccountRepository accountRepository;
	
	@AfterEach
	void afterEach() {
		accountRepository.deleteAll();
	}
	
	@Test
	@WithAccount("testUser")
	@DisplayName("View Event Form")
	void showEventForm() throws Exception {
		mockMvc.perform(get("/new-event"))
				.andExpect(status().isOk())
				.andExpect(view().name("event/form"))
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("eventForm"));
				
	}
	
	@Test
	@WithAccount("testUser")
	@DisplayName("Create Event - success")
	void createEvent_success() throws Exception{
		mockMvc.perform(post("/new-event")
				.param("path", "test-path")
				.param("title", "test-title")
				.param("shortDescription", "test-short-description")
				.param("fullDescription", "test-full-description-for-test-event")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/event/test-path"));		
	
	}
	
	@Test
	@WithAccount("testUser")
	@DisplayName("Create Event - fail")
	void createEvent_fail() throws Exception{
		mockMvc.perform(post("/new-event")
				.param("path", " ")
                .param("title", "test-title")
                .param("shortDescription", "test-short-description")
                .param("fullDescription", "test-full-description-for-test-event")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("event/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("eventForm"))
                .andExpect(model().attributeExists("account"));
		
		Event event = eventRepository.findByPath("test-path");
		assertNull(event);
	}
	
	
	@Test
	@WithAccount("testUser")
	@DisplayName("View Event Information")
	void viewEvent() throws Exception {
		Event event = new Event();
		event.setPath("test-path");
		event.setTitle("test-title");
		event.setShortDescription("test-short-description");
		event.setFullDescription("test-full-description-for-test-event");
		
		Account testUser = accountRepository.findByNickname("testUser");
		eventService.createNewEvent(event, testUser);
		
		mockMvc.perform(get("/event/test-path"))
			   .andExpect(view().name("event/view"))
			   .andExpect(model().attributeExists("account"))
			   .andExpect(model().attributeExists("event"));
			
	}
}