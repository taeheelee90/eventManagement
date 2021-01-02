package com.taeheelee.eventmanagement.modules.main;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.taeheelee.eventmanagement.modules.account.Account;
import com.taeheelee.eventmanagement.modules.account.CurrentUser;
import com.taeheelee.eventmanagement.modules.event.Event;
import com.taeheelee.eventmanagement.modules.event.EventRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {
	
	private final EventRepository eventRepository;

	@GetMapping("/")
	public String home(@CurrentUser Account account, Model model) {
		if(account != null) {
			model.addAttribute(account);
		}
		
		return "index";
		
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/search/study")
	public String searchEvent(String keyword, Model model) {
		List<Event> eventList = eventRepository.findByKeyword(keyword);
		model.addAttribute(eventList);
		model.addAttribute("keyword", keyword);
		return "search";
		
	}
}
