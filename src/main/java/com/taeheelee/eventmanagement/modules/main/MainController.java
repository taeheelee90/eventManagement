package com.taeheelee.eventmanagement.modules.main;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.taeheelee.eventmanagement.modules.account.Account;
import com.taeheelee.eventmanagement.modules.account.CurrentUser;

@Controller
public class MainController {

	@GetMapping("/")
	public String home(@CurrentUser Account account, Model model) {
		if(account != null) {
			model.addAttribute(account);
		}
		
		return "index";
		
	}
	
	@GetMapping("login")
	public String login() {
		return "login";
	}
}
