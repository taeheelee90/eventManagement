package com.taeheelee.eventmanagement.event;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.taeheelee.eventmanagement.Account.CurrentUser;
import com.taeheelee.eventmanagement.domain.Account;
import com.taeheelee.eventmanagement.domain.Event;
import com.taeheelee.eventmanagement.event.Form.EventDescriptionForm;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/event/{path}/settings")
@RequiredArgsConstructor
public class EventSettingController {
	
	private final EventService eventService;
	private final ModelMapper modelMapper;
	
	@GetMapping("/description")
	public String viewEventSetting(@CurrentUser Account account, @PathVariable String path, Model model) {
		Event event = eventService.getEventToUpdate(account, path);
		model.addAttribute(account);
		model.addAttribute(event);
		model.addAttribute(modelMapper.map(event, EventDescriptionForm.class));
		return "event/settings/description";
		
	}
	
	@PostMapping("/description")
	public String updateEventInfo (@CurrentUser Account account, @PathVariable String path, @Valid EventDescriptionForm eventDescriptionForm, Errors errors, Model model, RedirectAttributes attributes) {
		
		Event event = eventService.getEventToUpdate(account, path);
		
		if(errors.hasErrors()) {
			model.addAttribute(account);
			model.addAttribute(event);
			return "event/settings/description";
		}
		
		eventService.updateEventDescription(event, eventDescriptionForm);
		attributes.addFlashAttribute("message", "Updated Event Description.");
		return "redirect:/event/" + getPath(path) + "/settings/description";
				
	}

	private String getPath(String path) {
		return URLEncoder.encode(path, StandardCharsets.UTF_8);
	}

}
