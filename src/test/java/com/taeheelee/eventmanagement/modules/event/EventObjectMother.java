package com.taeheelee.eventmanagement.modules.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.taeheelee.eventmanagement.modules.account.Account;
import com.taeheelee.eventmanagement.modules.event.Event;
import com.taeheelee.eventmanagement.modules.event.EventRepository;
import com.taeheelee.eventmanagement.modules.event.EventService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventObjectMother {

	@Autowired EventService eventService;
	@Autowired EventRepository eventRepository;
	
	public Event createEvent(String path, Account manager) {
		Event event  = new Event();
		event.setPath(path);
		eventService.createNewEvent(event, manager);
		return event;
		
	}
}
