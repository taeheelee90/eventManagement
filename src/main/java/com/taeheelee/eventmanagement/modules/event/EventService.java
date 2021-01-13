package com.taeheelee.eventmanagement.modules.event;

import java.time.LocalDateTime;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taeheelee.eventmanagement.modules.account.Account;
import com.taeheelee.eventmanagement.modules.event.form.EventDescriptionForm;
import com.taeheelee.eventmanagement.modules.tag.Tag;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

	private final EventRepository eventRepository;
	private final RegistrationRepository registrationRepository;
	private final ModelMapper modelMapper;

	/*
	 * Event Create, Update, and Settings
	 * 
	 */

	public com.taeheelee.eventmanagement.modules.event.Event createNewEvent(
			com.taeheelee.eventmanagement.modules.event.Event event, Account account) {
		// com.taeheelee.eventmanagement.modules.event.Event newEvent =
		// eventRepository.save(event);
		event.setManager(account);
		event.setRegistration(true);

		return eventRepository.save(event);
	}

	public void updateEventDescription(Event event, @Valid EventDescriptionForm eventDescriptionForm) {
		modelMapper.map(eventDescriptionForm, event);

	}

	public boolean isValidTitle(String newTitle) {
		return newTitle.length() <= 50;
	}

	public void addTag(Event event, Tag tag) {
		event.getTags().add(tag);
		eventRepository.save(event);

	}

	public void removeTag(Event event, Tag tag) {
		event.getTags().remove(tag);
		eventRepository.save(event);

	}

	public void updateEventPath(Event event, String newPath) {
		event.setPath(newPath);
		eventRepository.save(event);

	}

	public void updateEventTitle(Event event, String newTitle) {
		event.setTitle(newTitle);
		eventRepository.save(event);

	}

	public void addMember(Event event, Account account) {

		Registration registration = new Registration();
		registration.setEnrolledAt(LocalDateTime.now());
		registration.setAccepted(event.hasSeats());
		registration.setAccount(account);
		event.addMember(account, registration);

		registrationRepository.save(registration);
		eventRepository.save(event);

	}

	public void removeMember(Event event, Account account) {

		Registration registration = registrationRepository.findByEventAndAccount(event, account);
		event.removeMember(account, registration);
		registrationRepository.delete(registration);
		eventRepository.save(event);

	}

	public void close(Event event) {
		event.close();

		eventRepository.save(event);
	}

	/*
	 * Get event
	 */
	public Event getEventToUpdate(Account account, String path) {
		Event event = this.getEvent(path);
		checkIfManager(account, event);
		return event;
	}

	public Event getEvent(String path) {
		Event event = this.eventRepository.findByPath(path);
		checkIfExistingEvent(path, event);
		return event;
	}

	public Event getEventToUpdateTag(Account account, String path) {
		Event event = eventRepository.findEventWithTagsByPath(path);
		checkIfExistingEvent(path, event);
		checkIfManager(account, event);
		return event;
	}

	public Event getEventToUpdateStatus(Account account, String path) {
		Event event = eventRepository.findEventWithManagerByPath(path);
		checkIfExistingEvent(path, event);
		checkIfManager(account, event);
		return event;
	}

	/*
	 * Check event
	 */

	public boolean isValidPath(String newPath) {
		if (!(newPath.length() >= 0 && newPath.length() <= 20)) {
			return false;
		}

		return !eventRepository.existsByPath(newPath);
	}

	private void checkIfManager(Account account, Event event) {
		if (!event.isMagedBy(account)) {
			throw new AccessDeniedException("This is only allowed for manager.");
		}

	}

	private void checkIfExistingEvent(String path, Event event) {
		if (event == null) {
			throw new IllegalArgumentException(path + " does not exist.");
		}

	}

}
