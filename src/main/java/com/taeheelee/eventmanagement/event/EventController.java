package com.taeheelee.eventmanagement.event;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
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
			return "event/form";
		}
		
		Event newEvent = eventService.createNewEvent(modelMapper.map(eventForm, Event.class), account);
		
		return "redirect:/event/" + newEvent.getPath();
	}
}
