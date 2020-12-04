package com.taeheelee.eventmanagement.Account;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AccountController {

	private final SignUpFormValidator signUpFormValidator;
	private final AccountService accountService;


	@InitBinder("signUpForm")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(signUpFormValidator);
	}

	@GetMapping("/sign-up")
	public String signUpForm(Model model) {
		model.addAttribute("signUpForm", new SignUpForm());
		return "account/sign-up.html";
	}

	@PostMapping("/sign-up")
	public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) {

		if (errors.hasErrors()) {
			return "account/sign-up.html";
		}

		accountService.processNewAccount(signUpForm);
		
		
		return "redirect:/";

	}

	
}
