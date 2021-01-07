package com.taeheelee.eventmanagement.modules.event.validator;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.taeheelee.eventmanagement.modules.event.EventRepository;
import com.taeheelee.eventmanagement.modules.event.form.EventForm;

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
	
		
		if (invalidEndRegistrationDateTime(eventForm)) {
			errors.rejectValue("endRegistrationDateTime", "wrong.datetime", "Please check the due date of registration.");
		}

		if (invalidEventEndDateTime(eventForm)) {
			errors.rejectValue("eventEndDateTime", "wrong.datetime", "Please check end date and time.");
		}

		if (invalidEventStartDateTime(eventForm)) {
			errors.rejectValue("eventStartDateTime", "wrong.datetime", "Please check start date adn time.");
		}

		if (eventRepository.existsByPath(eventForm.getPath())) {
			errors.rejectValue("path", "wrong.path", "Path is already in use.");
		}

	}

	private boolean invalidEventStartDateTime(EventForm eventForm) {

		return eventForm.getEventStartDateTime().isBefore(eventForm.getEndRegistrationDateTime());
	}

	private boolean invalidEventEndDateTime(EventForm eventForm) {
		LocalDateTime eventEndDateTime = eventForm.getEventEndDateTime();

		return eventEndDateTime.isBefore(eventForm.getEventStartDateTime())
				|| eventEndDateTime.isBefore(eventForm.getEndRegistrationDateTime());

	}

	private boolean invalidEndRegistrationDateTime(EventForm eventForm) {

		return eventForm.getEndRegistrationDateTime().isBefore(LocalDateTime.now());
	}

}
