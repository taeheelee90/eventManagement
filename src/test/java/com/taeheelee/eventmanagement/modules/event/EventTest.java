package com.taeheelee.eventmanagement.modules.event;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.taeheelee.eventmanagement.modules.account.Account;
import com.taeheelee.eventmanagement.modules.account.UserAccount;
import com.taeheelee.eventmanagement.modules.event.Event;

class EventTest {

	Event event;
	Account account;
	UserAccount userAccount;

	@BeforeEach
	void beforeEach() {
		event = new Event();
		account = new Account();
		account.setNickname("testUser");
		account.setPassword("12345678");
		userAccount = new UserAccount(account);
	}

	@DisplayName("Published, Recuriting, Not a member nor manager - can join")
	@Test
	void canJoin() {
		event.setPublished(true);
		event.setRegistration(true);

		assertTrue(event.isJoinable(userAccount));
	}

	@DisplayName("Published, Recruiting, Manager - cannot join")
	@Test
	void cannotJoin_manager() {
		event.setPublished(true);
		event.setRegistration(true);
		event.addManager(account);

		assertFalse(event.isJoinable(userAccount));
	}

	@DisplayName("Published, Recruting, Member - cannot join again")
	@Test
	void cannotJoin_member() {
		event.setPublished(true);
		event.setRegistration(true);
		event.addMember(account);

		assertFalse(event.isJoinable(userAccount));
	}

	@DisplayName("Not published, Not recruting - cannot join")
	@Test
	void cannotJoin_not_published_not_recruting() {
		event.setPublished(false);
		event.setRegistration(false);

		assertFalse(event.isJoinable(userAccount));

		event.setPublished(false);
		event.setRegistration(true);

		assertFalse(event.isJoinable(userAccount));
	}

	@DisplayName("Check isManager")
	@Test
	void isManager() {
		event.addManager(account);
		assertTrue(event.isManager(userAccount));
	}

	@DisplayName("Check isMember")
	@Test
	void isMember() {
		event.addMember(account);
		assertTrue(event.isMember(userAccount));
	}
}
