package com.taeheelee.eventmanagement.settings;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.taeheelee.eventmanagement.Account.CurrentUser;
import com.taeheelee.eventmanagement.domain.Account;

@Controller
public class SettingController {

	
	@GetMapping("/settings/profile")
	public String profileUpdateForm(@CurrentUser Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(new Profile(account));
		
		return "settings/profile";
	}
}
