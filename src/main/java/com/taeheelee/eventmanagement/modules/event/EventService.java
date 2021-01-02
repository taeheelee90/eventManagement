package com.taeheelee.eventmanagement.modules.event;

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
import com.taeheelee.eventmanagement.modules.zone.Zone;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

	private final EventRepository eventRepository;
	private final ModelMapper modelMapper;
	private final ApplicationEventPublisher appEventPublisher;

	public com.taeheelee.eventmanagement.modules.event.Event createNewEvent(com.taeheelee.eventmanagement.modules.event.Event event,
			Account account) {
		com.taeheelee.eventmanagement.modules.event.Event newEvent = eventRepository.save(event);
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
		checkIfExistingEvent(path, event);
		return event;
	}

	public void updateEventDescription(Event event, @Valid EventDescriptionForm eventDescriptionForm) {
		modelMapper.map(eventDescriptionForm, event);
		appEventPublisher.publishEvent(new EventUpdated(event, "Event description has been updated"));
	}


	public void addTag(Event event, Tag tag) {
		event.getTags().add(tag);

	}

	public void removeTag(Event event, Tag tag) {
		event.getTags().remove(tag);

	}

	public void addZone(Event event, Zone zone) {
		event.getZones().add(zone);
	}

	public void removeZone(Event event, Zone zone) {
		event.getZones().remove(zone);
	}

	public Event getEventToUpdateTag(Account account, String path) {
		Event event = eventRepository.findEventWithTagsByPath(path);
		checkIfExistingEvent(path, event);
		checkIfManager(account, event);
		return event;
	}

	public Event getEventToUpdateZone(Account account, String path) {
		Event event = eventRepository.findEventWithZonesByPath(path);
		checkIfExistingEvent(path, event);
		checkIfManager(account, event);
		return event;
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

	public Event getEventToUpdateStatus(Account account, String path) {
		Event event = eventRepository.findEventWithManagersByPath(path);
		checkIfExistingEvent(path, event);
		checkIfManager(account, event);
		return event;
	}

	public void publish(Event event) {
		event.publish();
		this.appEventPublisher.publishEvent(new EventCreated(event));

	}

	public void close(Event event) {
		event.close();
		appEventPublisher.publishEvent(new EventUpdated(event, "Event is closed."));
	}

	public void startRegistration(Event event) {
		event.startRegistration();
		appEventPublisher.publishEvent(new EventUpdated(event, "Event started recruiting members."));
	}

	public void stopRegistration(Event event) {
		event.stopRegistration();
		appEventPublisher.publishEvent(new EventUpdated(event, "Event finished recruiting members."));

	}

	public boolean isValidPath(String newPath) {
		if (!(newPath.length() >= 0 && newPath.length() <= 20)) {
			return false;
		}

		return !eventRepository.existsByPath(newPath);
	}

	public void updateEventPath(Event event, String newPath) {
		event.setPath(newPath);

	}
	
	public boolean isValidTitle(String newTitle) {
		return newTitle.length() <= 50;
	}

	public void updateEventTitle(Event event, String newTitle) {
		event.setTitle(newTitle);
		
	}

	public void remove(Event event) {
		if(event.isRemovable()) {
			eventRepository.delete(event);
		} else {
			throw new IllegalArgumentException("Can not delete event.");
		}
		
	}

	public void addMember(Event event, Account account) {
		event.addMember(account);
		
	}

	public void removeMember(Event event, Account account) {
		event.removeMember(account);
	}

	public Event getEventToEnroll(String path) {
		Event event = eventRepository.findEventOnlyByPath(path);
		checkIfExistingEvent(path, event);
		return event;
	}

}
