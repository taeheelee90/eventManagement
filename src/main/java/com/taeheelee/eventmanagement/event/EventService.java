package com.taeheelee.eventmanagement.event;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taeheelee.eventmanagement.domain.Account;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {
	
	private final EventRepository eventRepository;


	public com.taeheelee.eventmanagement.domain.Event createNewEvent(com.taeheelee.eventmanagement.domain.Event event,
			Account account) {
		com.taeheelee.eventmanagement.domain.Event newEvent = eventRepository.save(event);
		newEvent.addManager(account);
		
		return newEvent;
	}


	
}
