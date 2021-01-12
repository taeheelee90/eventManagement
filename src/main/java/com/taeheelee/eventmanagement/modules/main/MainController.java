package com.taeheelee.eventmanagement.modules.main;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
		model.addAttribute("eventList", eventRepository.findFirst9ByRegistrationOrderByEventStartDateTimeDesc(true));
		return "index";
		
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/search/event")
	public String searchEvent(@PageableDefault(size=9, sort="publishedDateTime", direction=Sort.Direction.DESC) Pageable pageable, String keyword, Model model) {
		Page<Event> eventPage = eventRepository.findByKeyword(keyword, pageable);
		model.addAttribute("eventPage", eventPage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("sortProperty", pageable.getSort().toString().contains("publishedDateTime") ? "publishedDateTime" : "memberCount");
		return "search";
		
	}
}
