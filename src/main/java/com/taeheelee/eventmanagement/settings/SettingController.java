package com.taeheelee.eventmanagement.settings;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taeheelee.eventmanagement.Account.AccountRepository;
import com.taeheelee.eventmanagement.Account.AccountService;
import com.taeheelee.eventmanagement.Account.CurrentUser;
import com.taeheelee.eventmanagement.domain.Account;
import com.taeheelee.eventmanagement.domain.Tag;
import com.taeheelee.eventmanagement.domain.Zone;
import com.taeheelee.eventmanagement.settings.form.NicknameForm;
import com.taeheelee.eventmanagement.settings.form.Notifications;
import com.taeheelee.eventmanagement.settings.form.PasswordForm;
import com.taeheelee.eventmanagement.settings.form.Profile;
import com.taeheelee.eventmanagement.settings.form.TagForm;
import com.taeheelee.eventmanagement.settings.form.ZoneForm;
import com.taeheelee.eventmanagement.settings.validator.NicknameFormValidator;
import com.taeheelee.eventmanagement.settings.validator.PasswordFormValidatort;
import com.taeheelee.eventmanagement.tag.TagRepository;
import com.taeheelee.eventmanagement.tag.TagService;
import com.taeheelee.eventmanagement.zone.ZoneRepository;
import com.taeheelee.eventmanagement.zone.ZoneService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SettingController {
	
	private final AccountService accountService;
	private final TagService tagService;
	private final ZoneService zoneService;
	private final AccountRepository accountRepository;
	private final ModelMapper modelMapper;
	private final NicknameFormValidator nicknameFormValidator;
	private final TagRepository tagRepository;
	private final ZoneRepository zoneRepository;
	private final ObjectMapper objectMapper;

	@InitBinder("passwordForm")
	public void pwFormInitBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(new PasswordFormValidatort());
	}

	@InitBinder("nicknameForm")
	public void nicknameFormInitBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(nicknameFormValidator);
	}

	static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
	static final String SETTINGS_PROFILE_URL = "/settings/profile";

	static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
	static final String SETTINGS_PASSWORD_URL = "/settings/password";

	static final String SETTINGS_NOTIFICATIONS_VIEW_NAME = "settings/notifications";
	static final String SETTINGS_NOTIFICATIONS_URL = "/settings/notifications";

	static final String SETTINGS_ACCOUNT_VIEW_NAME = "settings/account";
	static final String SETTINGS_ACCOUNT_URL = "/settings/account";

	static final String SETTINGS_TAGS_VIEW_NAME = "settings/tags";
	static final String SETTINGS_TAGS_URL = "/settings/tags";
	
	static final String SETTINGS_ZONES_VIEW_NAME = "settings/zones";
	static final String SETTINGS_ZONES_URL = "/settings/zones";

	@GetMapping(SETTINGS_PROFILE_URL)
	public String profileUpdateForm(@CurrentUser Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(modelMapper.map(account, Profile.class));
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
		model.addAttribute(modelMapper.map(account, Notifications.class));
		return SETTINGS_NOTIFICATIONS_VIEW_NAME;
	}

	@PostMapping(SETTINGS_NOTIFICATIONS_URL)
	public String updateNotifications(@CurrentUser Account account, @Valid Notifications notifications, Errors errors,
			Model model, RedirectAttributes attributes) {

		if (errors.hasErrors()) {
			model.addAttribute(account);
			return SETTINGS_NOTIFICATIONS_VIEW_NAME;
		}

		accountService.updateNotifications(account, notifications);
		attributes.addFlashAttribute("message", "Updated Notifications.");
		return "redirect:" + SETTINGS_NOTIFICATIONS_URL;

	}

	@GetMapping(SETTINGS_ACCOUNT_URL)
	public String updateNicknameForm(@CurrentUser Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(modelMapper.map(account, NicknameForm.class));
		return SETTINGS_ACCOUNT_VIEW_NAME;
	}

	@PostMapping(SETTINGS_ACCOUNT_URL)
	public String updateNickname(@CurrentUser Account account, @Valid NicknameForm nicknameForm, Errors errors,
			Model model, RedirectAttributes attributes) {

		if (errors.hasErrors()) {
			model.addAttribute(account);
			return SETTINGS_ACCOUNT_VIEW_NAME;
		}

		accountService.updateNickname(account, nicknameForm);
		attributes.addFlashAttribute("message", "Updated Nickname.");
		return "redirect:" + SETTINGS_ACCOUNT_URL;
	}

	@GetMapping(SETTINGS_TAGS_URL)
	public String updateTagsForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
		model.addAttribute(account);
		
		Set<Tag> tags = accountService.getTags(account);
		model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));
		
		List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
		model.addAttribute("whiteList", objectMapper.writeValueAsString(allTags));
		
		return SETTINGS_TAGS_VIEW_NAME;
	}

	@PostMapping(SETTINGS_TAGS_URL + "/add")
	@ResponseBody
	public ResponseEntity addTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
		Tag tag = tagService.findOrCreateNew(tagForm.getTitle());
		accountService.addTag(account, tag);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping(SETTINGS_TAGS_URL + "/remove")
	@ResponseBody
	public ResponseEntity removeTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
		String title = tagForm.getTitle();

		Tag tag = tagRepository.findByTitle(title);
		if (tag == null) {
			return ResponseEntity.badRequest().build();
		}

		accountService.removeTag(account, tag);
		return ResponseEntity.ok().build();
	}
	
	
	@GetMapping(SETTINGS_ZONES_URL)
	public String updateZonesForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
		model.addAttribute(account);
		
		Set<Zone> zones = accountService.getZones(account);
		model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));
		
		List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
		model.addAttribute("whiteList", objectMapper.writeValueAsString(allZones));
		
		return SETTINGS_ZONES_VIEW_NAME;
	}
	
	@PostMapping(SETTINGS_ZONES_URL + "/add")
	@ResponseBody
	public ResponseEntity addZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
		Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
		
		if(zone == null) {
			return ResponseEntity.badRequest().build();
		}
		
		accountService.addZone(account,zone);
		return ResponseEntity.ok().build();
	}

	
	@PostMapping(SETTINGS_ZONES_URL + "/remove")
	@ResponseBody
	public ResponseEntity removeZone (@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
		Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
		if(zone == null) {
			return ResponseEntity.badRequest().build();
		}
		
		accountService.removeZone(account, zone);
		return ResponseEntity.ok().build();
	}
}
