package com.taeheelee.eventmanagement.settings;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.taeheelee.eventmanagement.Account.AccountService;
import com.taeheelee.eventmanagement.Account.CurrentUser;
import com.taeheelee.eventmanagement.domain.Account;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SettingController {

	@InitBinder("passwordForm")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(new PasswordFormValidatort());
	}

	static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
	static final String SETTINGS_PROFILE_URL = "/settings/profile";

	static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
	static final String SETTINGS_PASSWORD_URL = "/settings/password";

	static final String SETTINGS_NOTIFICATIONS_VIEW_NAME = "settings/notifications";
	static final String SETTINGS_NOTIFICATIONS_URL = "/settings/notifications";

	private final AccountService accountService;

	@GetMapping(SETTINGS_PROFILE_URL)
	public String profileUpdateForm(@CurrentUser Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(new Profile(account));
		return SETTINGS_PROFILE_VIEW_NAME;
	}

	@PostMapping(SETTINGS_PROFILE_URL)
	public String updateProfile(@CurrentUser Account account, @Valid Profile profile, Errors errors, Model model,
			RedirectAttributes attributes) {
		if (errors.hasErrors()) {
			model.addAttribute(account);
			return SETTINGS_PROFILE_VIEW_NAME;
		}

		accountService.updateProfile(account, profile);
		attributes.addFlashAttribute("message", "Updated profile.");
		return "redirect:" + SETTINGS_PROFILE_URL;
	}

	@GetMapping(SETTINGS_PASSWORD_URL)
	public String passwordUpdateForm(@CurrentUser Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(new PasswordForm());
		return SETTINGS_PASSWORD_VIEW_NAME;
	}

	@PostMapping(SETTINGS_PASSWORD_URL)
	public String updatePassword(@CurrentUser Account account, @Valid PasswordForm passwordForm, Errors errors,
			Model model, RedirectAttributes attributes) {
		if (errors.hasErrors()) {
			model.addAttribute(account);
			return SETTINGS_PASSWORD_VIEW_NAME;
		}

		accountService.updatePassword(account, passwordForm.getNewPassword());
		attributes.addFlashAttribute("message", "Updated Password.");
		return "redirect:" + SETTINGS_PASSWORD_URL;
	}

	@GetMapping(SETTINGS_NOTIFICATIONS_URL)
	public String updateNotificationForm(@CurrentUser Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(new Notifications(account));
		return SETTINGS_NOTIFICATIONS_VIEW_NAME;
	}

	@PostMapping(SETTINGS_NOTIFICATIONS_URL)
	public String updateNotifications(@CurrentUser Account account, @Valid Notifications notifications, Errors errors,
			Model model, RedirectAttributes attributes) {
		
		if(errors.hasErrors()) {
			model.addAttribute(account);
			return SETTINGS_NOTIFICATIONS_VIEW_NAME;
		}
		
		accountService.updateNotifications(account, notifications);
		attributes.addFlashAttribute("message", "Updated Notifications.");
		return "redirect:" + SETTINGS_NOTIFICATIONS_URL;
		
	}

}
