package com.taeheelee.eventmanagement.main;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.taeheelee.eventmanagement.Account.CurrentUser;
import com.taeheelee.eventmanagement.domain.Account;

@Controller
public class MainController {

	@GetMapping("/")
	public String home(@CurrentUser Account account, Model model) {
		if(account != null) {
			model.addAttribute(account);
		}
		
		return "index";
		
	}
}
