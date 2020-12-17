package com.taeheelee.eventmanagement.event.validator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.taeheelee.eventmanagement.event.EventRepository;
import com.taeheelee.eventmanagement.event.Form.EventForm;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EventFormValidator implements Validator {
	
	private final EventRepository eventRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return EventForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		EventForm eventForm = (EventForm) target;
		if(eventRepository.existsByPath(eventForm.getPath())) {
			errors.rejectValue("path","wrong.path", "Path is already in use.");
		}
		
	}
	
	

}
