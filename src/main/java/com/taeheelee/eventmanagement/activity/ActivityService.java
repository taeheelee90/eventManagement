package com.taeheelee.eventmanagement.activity;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taeheelee.eventmanagement.domain.Account;
import com.taeheelee.eventmanagement.domain.Activity;
import com.taeheelee.eventmanagement.domain.Event;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ActivityService {

	private final ActivityRepository activityRepository;
	
	public Activity createEvent(Activity activity, Event event, Account account) {
		activity.setCreatedBy(account);
		activity.setCreatedDateTime(LocalDateTime.now());
		activity.setEvent(event);
		
		return activityRepository.save(activity);
	}


}
