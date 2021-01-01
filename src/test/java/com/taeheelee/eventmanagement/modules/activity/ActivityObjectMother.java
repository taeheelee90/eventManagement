package com.taeheelee.eventmanagement.modules.activity;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.taeheelee.eventmanagement.modules.account.Account;
import com.taeheelee.eventmanagement.modules.event.Event;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ActivityObjectMother {

	@Autowired
	ActivityService activityService;

	public Activity createActivity(String title, ActivityType type, int limit, Event event, Account account) {
		Activity activity = new Activity();
		activity.setActivityType(type);
		activity.setLimitOfEnrollments(limit);
		activity.setTitle(title);
		activity.setCreatedDateTime(LocalDateTime.now());
		activity.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
		activity.setStartDateTime(LocalDateTime.now().plusDays(1).plusHours(3));
		activity.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(5));
		return activityService.createActivity(activity, event, account);

	}
}
