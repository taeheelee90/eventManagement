package com.taeheelee.eventmanagement.event;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taeheelee.eventmanagement.domain.Account;
import com.taeheelee.eventmanagement.domain.Event;
import com.taeheelee.eventmanagement.event.Form.EventDescriptionForm;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {
	
	private final EventRepository eventRepository;
	private final ModelMapper modelMapper;
	//private final ApplicationEventPublisher appEventPublisher;


	public com.taeheelee.eventmanagement.domain.Event createNewEvent(com.taeheelee.eventmanagement.domain.Event event,
			Account account) {
		com.taeheelee.eventmanagement.domain.Event newEvent = eventRepository.save(event);
		newEvent.addManager(account);
		
		return newEvent;
	}


	public Event getEventToUpdate(Account account, String path) {
		Event event = this.getEvent(path);
		checkIfManager(account, event);
		return event;
	}


	public Event getEvent(String path) {
		Event event = this.eventRepository.findByPath(path);
		checkIfExistingStudy(path, event);
		return event;
	}


	private void checkIfManager(Account account, Event event) {
		if(!event.isMagedBy(account)) {
			throw new AccessDeniedException("This is only allowed for manager.");
		}
		
	}


	private void checkIfExistingStudy(String path, Event event) {
		if (event == null) {
			throw new IllegalArgumentException(path + " does not exist.");
		}
		
	}


	public void updateEventDescription(Event event, @Valid EventDescriptionForm eventDescriptionForm) {
		modelMapper.map(eventDescriptionForm, event);
		//appEventPublisher.publishEvent(new EventUpdate(event, "Updated Event Description"));
		
	}


	
}
