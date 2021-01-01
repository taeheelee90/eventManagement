package com.taeheelee.eventmanagement.modules.activity;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;
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
import com.taeheelee.eventmanagement.modules.event.EventObjectMother;

@MockMvcForTest
public class ActivityControllerTest {

	@Autowired MockMvc mockMvc;
	@Autowired ActivityObjectMother activityObjectMother;
	@Autowired EventObjectMother eventObjectMother;
	@Autowired AccountObjectMother accountObjectMother;
	@Autowired ActivityService activityService;
	@Autowired EnrollmentRepository enrollmentRepository;
	@Autowired AccountRepository accountRepository;
	
	@Test
	@DisplayName("Register for FCFS - auto accept")
	@WithAccount("testUser")
	void enrollment_FCFS_accepted() throws Exception {
		Account newAccount =accountObjectMother.createAccount("newAccount");
		Event event = eventObjectMother.createEvent("test-path", newAccount);
		Activity activity = activityObjectMother.createActivity("test-activity", ActivityType.FCFS, 2, event, newAccount);
		
		mockMvc.perform(post("/event/" + event.getPath() + "/activities/" + activity.getId() + "/enroll" )
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/event/" + event.getPath() + "/activities/" + activity.getId()));
		
		Account testUser = accountRepository.findByNickname("testUser");
		isAccepted(testUser, activity);
	}
	
	@Test
	@DisplayName("Register for FCFS - waiting(full)")
	@WithAccount("testUser")
	void enrollment_FCFS_waiting() throws Exception {
		Account newAccount =accountObjectMother.createAccount("newAccount");
		Event event = eventObjectMother.createEvent("test-path", newAccount);
		Activity activity = activityObjectMother.createActivity("test-activity", ActivityType.FCFS, 2, event, newAccount);
		
		Account test1 = accountObjectMother.createAccount("test1");
		Account test2 = accountObjectMother.createAccount("test2");
		activityService.enroll(activity, test1);
		activityService.enroll(activity, test2);
		
		mockMvc.perform(post("/event/" + event.getPath() + "/activities/" + activity.getId() + "/enroll")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/event/" + event.getPath() + "/activities/" + activity.getId()));
		
		Account test3 = accountRepository.findByNickname("testUser");
		isNotAccepted(test3, activity);
		
	}
	

	private void isNotAccepted(Account testUser, Activity activity) {
		assertFalse(enrollmentRepository.findByActivityAndAccount(activity, testUser).isAccepted());
		
	}

	private void isAccepted(Account testUser, Activity activity) {
		assertTrue(enrollmentRepository.findByActivityAndAccount(activity, testUser).isAccepted());
		
	}
}
