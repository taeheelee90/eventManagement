package com.taeheelee.eventmanagement.modules.event;

import java.time.LocalDateTime;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taeheelee.eventmanagement.modules.account.Account;

import com.taeheelee.eventmanagement.modules.event.eventPublisher.EventCreated;
import com.taeheelee.eventmanagement.modules.event.eventPublisher.EventUpdated;
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
	private final ApplicationEventPublisher appEventPublisher;

	/*
	 * Event Create, Update, and Settings
	 * 
	 */

	public com.taeheelee.eventmanagement.modules.event.Event createNewEvent(
			com.taeheelee.eventmanagement.modules.event.Event event, Account account) {
		com.taeheelee.eventmanagement.modules.event.Event newEvent = eventRepository.save(event);
		newEvent.setManager(account);
		newEvent.setRegistration(true);
		return newEvent;
	}

	public void updateEventDescription(Event event, @Valid EventDescriptionForm eventDescriptionForm) {
		modelMapper.map(eventDescriptionForm, event);
		appEventPublisher.publishEvent(new EventUpdated(event, "Event description has been updated"));
	}

	public boolean isValidTitle(String newTitle) {
		return newTitle.length() <= 50;
	}

	public void addTag(Event event, Tag tag) {
		event.getTags().add(tag);

	}

	public void removeTag(Event event, Tag tag) {
		event.getTags().remove(tag);

	}

	public void updateEventPath(Event event, String newPath) {
		event.setPath(newPath);

	}

	public void updateEventTitle(Event event, String newTitle) {
		event.setTitle(newTitle);

	}

	/*
	 * 
	 * public Event getEventToEnroll(String path) { Event event =
	 * eventRepository.findEventOnlyByPath(path); checkIfExistingEvent(path, event);
	 * return event; }
	 * 
	 * 
	 * public void enroll(Event event, Account account) { if
	 * (!registrationRepository.existsByEventAndAccount(event, account)) {
	 * Registration registration = new Registration();
	 * registration.setEnrolledAt(LocalDateTime.now());
	 * registration.setAccepted(event.isAbleToAcceptWaitingRegistration());
	 * registration.setAccount(account); event.addRegistration(registration);
	 * registrationRepository.save(registration); }
	 * 
	 * }
	 * 
	 * public void disenroll(Event event, Account account) { Registration
	 * registration = registrationRepository.findByEventAndAccount(event, account);
	 * 
	 * event.removeregistration(registration);
	 * registrationRepository.delete(registration);
	 * event.acceptNextWaitingregistration();
	 * 
	 * }
	 */

	public void addMember(Event event, Account account) {
		

			Registration registration = new Registration();
			registration.setEnrolledAt(LocalDateTime.now());
			registration.setAccepted(event.hasSeats());
			registration.setAccount(account);	
			event.addMember(account, registration);
			
			registrationRepository.save(registration);
		

	}

	public void removeMember(Event event, Account account) {

		Registration registration = registrationRepository.findByEventAndAccount(event, account);
		event.removeMember(account, registration);
		registrationRepository.delete(registration);
		
	}

	public void close(Event event) {
		event.close();
		appEventPublisher.publishEvent(new EventUpdated(event, "Event is closed."));
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

	/*
	 * creating random data
	 * 
	 * public void generateTestData(Account account) { for (int i = 0; i < 10; i++)
	 * { String randomString = RandomString.make(6); Event event =
	 * Event.builder().title("Web Event " + i + randomString).path("web-" + i +
	 * randomString)
	 * .shortDescription("test").fullDescription("testtesttest").tags(new
	 * HashSet<>()) .managers(new HashSet<>()).build();
	 * 
	 * 
	 * Event newEvent = this.createNewEvent(event, account); Tag web =
	 * tagRepository.findByTitle("WEB"); newEvent.getTags().add(web);
	 * 
	 * }
	 * 
	 * for (int i = 0; i < 10; i++) { String randomString = RandomString.make(6);
	 * Event event = Event.builder() .title("Cloud Event " + i + randomString)
	 * .path("cloud-" + i + randomString) .shortDescription("test")
	 * .fullDescription("testtesttest") .registrationType(RegistrationType.FCFS)
	 * .limitOfRegistrations(5) .startRegistrationDateTime(LocalDateTime.now())
	 * .endRegistrationDateTime(LocalDateTime.now().plusDays(3))
	 * .eventStartDateTime(LocalDateTime.now().plusDays(5))
	 * .eventEndDateTime(LocalDateTime.now().plusDays(6)) .tags(new HashSet<>())
	 * .managers(new HashSet<>()).build();
	 * 
	 * event.publish(); Event newEvent = this.createNewEvent(event, account); Tag
	 * cloud = tagRepository.findByTitle("CLOUD"); newEvent.getTags().add(cloud);
	 * 
	 * }
	 * 
	 * for (int i = 0; i < 10; i++) { String randomString = RandomString.make(6);
	 * Event event = Event.builder() .title("AWS Event " + i + randomString)
	 * .path("aws-" + i + randomString) .shortDescription("test")
	 * .fullDescription("testtesttest") .registrationType(RegistrationType.FCFS)
	 * .limitOfRegistrations(5) .startRegistrationDateTime(LocalDateTime.now())
	 * .endRegistrationDateTime(LocalDateTime.now().plusDays(3))
	 * .eventStartDateTime(LocalDateTime.now().plusDays(5))
	 * .eventEndDateTime(LocalDateTime.now().plusDays(6)) .tags(new HashSet<>())
	 * .managers(new HashSet<>()).build();
	 * 
	 * event.publish(); Event newEvent = this.createNewEvent(event, account); Tag
	 * aws = tagRepository.findByTitle("AWS"); newEvent.getTags().add(aws);
	 * 
	 * }
	 * 
	 * 
	 * 
	 * }
	 */

	

}
