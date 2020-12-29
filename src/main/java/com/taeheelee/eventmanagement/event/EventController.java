package com.taeheelee.eventmanagement.event;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.taeheelee.eventmanagement.Account.CurrentUser;
import com.taeheelee.eventmanagement.domain.Account;
import com.taeheelee.eventmanagement.domain.Event;
import com.taeheelee.eventmanagement.event.Form.EventForm;
import com.taeheelee.eventmanagement.event.validator.EventFormValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class EventController {

	private final EventRepository eventRepository;
	private final EventService eventService;
	private final ModelMapper modelMapper;
	private final EventFormValidator eventFormValidator;
	
	@InitBinder ("eventForm")
	public void eventFormInitBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(eventFormValidator);
	}
	
	@GetMapping("/new-event")
	public String newEventForm(@CurrentUser Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(new EventForm());
		
		return "event/form";
		
	}
	
	@PostMapping("/new-event")
	public String newEventSubmit(@CurrentUser Account account, @Valid EventForm eventForm, Errors errors, Model model) {
		if(errors.hasErrors()) {
			model.addAttribute(account);
			return "event/form";
		}
		
		Event newEvent = eventService.createNewEvent(modelMapper.map(eventForm, Event.class), account);
		
		return "redirect:/event/" + newEvent.getPath();
	}
	
	@GetMapping("/event/{path}")
	public String viewEvent(@CurrentUser Account account, @PathVariable String path, Model model) {
		Event event = eventService.getEvent(path);
		model.addAttribute(account);
		model.addAttribute(event);
		return "event/view";
	}
	
	@GetMapping("/event/{path}/members")
	public String viewMembers (@CurrentUser Account account, @PathVariable String path, Model model) {
		Event event = eventService.getEvent(path);
		model.addAttribute(account);
		model.addAttribute(event);
		return "event/members";
	}
	
	
	@GetMapping("/event/{path}/join")
	public String joinEvent(@CurrentUser Account account, @PathVariable String path) {
		Event event = eventRepository.findEventWithMembersByPath(path);
		eventService.addMember(event, account);
		return "redirect:/event/" + event.getEncodedPath() + "/members";
	}
	
	@GetMapping("/event/{path}/leave")
	public String leaveEvent(@CurrentUser Account account, @PathVariable String path) {
		Event event = eventRepository.findEventWithMembersByPath(path);
		eventService.removeMember(event, account);
		return "redirect:/event/" + event.getEncodedPath() + "/members";
	}
}
