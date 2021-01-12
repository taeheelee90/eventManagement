package com.taeheelee.eventmanagement.modules.event;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taeheelee.eventmanagement.modules.account.Account;
import com.taeheelee.eventmanagement.modules.account.CurrentUser;
import com.taeheelee.eventmanagement.modules.account.form.TagForm;

import com.taeheelee.eventmanagement.modules.event.form.EventDescriptionForm;
import com.taeheelee.eventmanagement.modules.tag.Tag;
import com.taeheelee.eventmanagement.modules.tag.TagRepository;
import com.taeheelee.eventmanagement.modules.tag.TagService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/event/{path}/settings")
@RequiredArgsConstructor
public class EventSettingController {

	private final TagRepository tagRepository;
	private final TagService tagService;
	private final EventService eventService;
	private final ModelMapper modelMapper;
	private final ObjectMapper objectMapper;

	@GetMapping("/description")
	public String viewEventSetting(@CurrentUser Account account, @PathVariable String path, Model model) {
		Event event = eventService.getEventToUpdate(account, path);
		model.addAttribute(account);
		model.addAttribute(event);
		model.addAttribute(modelMapper.map(event, EventDescriptionForm.class));
		return "event/settings/description";

	}

	@PostMapping("/description")
	public String updateEventInfo(@CurrentUser Account account, @PathVariable String path,
			@Valid EventDescriptionForm eventDescriptionForm, Errors errors, Model model,
			RedirectAttributes attributes) {

		Event event = eventService.getEventToUpdate(account, path);

		if (errors.hasErrors()) {
			model.addAttribute(account);
			model.addAttribute(event);
			return "event/settings/description";
		}

		eventService.updateEventDescription(event, eventDescriptionForm);
		attributes.addFlashAttribute("message", "Updated Event Description.");
		return "redirect:/event/" + event.getEncodedPath() + "/settings/description";

	}

	@GetMapping("/tags")
	public String eventTagsForm(@CurrentUser Account account, @PathVariable String path, Model model)
			throws JsonProcessingException {
		Event event = eventService.getEventToUpdate(account, path);
		model.addAttribute(account);
		model.addAttribute(event);

		model.addAttribute("tags", event.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));
		List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
		model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));
		return "event/settings/tags";
	}

	@PostMapping("/tags/add")
	@ResponseBody
	public ResponseEntity addTag(@CurrentUser Account account, @PathVariable String path,
			@RequestBody TagForm tagForm) {
		Event event = eventService.getEventToUpdateTag(account, path);
		Tag tag = tagService.findOrCreateNew(tagForm.getTitle());
		eventService.addTag(event, tag);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/tags/remove")
	@ResponseBody
	public ResponseEntity removeTag(@CurrentUser Account account, @PathVariable String path,
			@RequestBody TagForm tagForm) {
		Event event = eventService.getEventToUpdateTag(account, path);
		Tag tag = tagRepository.findByTitle(tagForm.getTitle());
		if (tag == null) {
			return ResponseEntity.badRequest().build();
		}

		eventService.removeTag(event, tag);
		return ResponseEntity.ok().build();
	}

	
	@GetMapping("/event")
	public String eventStatusForm(@CurrentUser Account account, @PathVariable String path, Model model) {
		Event event = eventService.getEventToUpdate(account, path);
		model.addAttribute(account);
		model.addAttribute(event);
		return "event/settings/event";
	}

	
	@PostMapping("/event/close")
	public String closeEvent(@CurrentUser Account account, @PathVariable String path, RedirectAttributes attributes) {
		Event event = eventService.getEventToUpdateStatus(account, path);
		eventService.close(event);
		attributes.addFlashAttribute("message", "Closed event.");
		return "redirect:/event/" + event.getEncodedPath() + "/settings/event";
	}

	
	@PostMapping("/event/path")
	public String updateEventPath(@CurrentUser Account account, @PathVariable String path, String newPath, Model model,
			RedirectAttributes attributes) {
		Event event = eventService.getEventToUpdateStatus(account, path);
		if (!eventService.isValidPath(newPath)) {
			model.addAttribute(account);
			model.addAttribute(event);
			model.addAttribute("eventPathError", "Can not use tihs path.");
			return "event/settings/event";
		}

		eventService.updateEventPath(event, newPath);
		attributes.addFlashAttribute("message", "Updated event path.");
		return "redirect:/event/" + event.getEncodedPath() + "/settings/event";
	}

	@PostMapping("/event/title")
	public String updateEventTitle(@CurrentUser Account account, @PathVariable String path, String newTitle,
			Model model, RedirectAttributes attributes) {
		Event event = eventService.getEventToUpdateStatus(account, path);
		if (!eventService.isValidTitle(newTitle)) {
			model.addAttribute(account);
			model.addAttribute(event);
			model.addAttribute("eventTitleError", "Please enter title again.");
			return "event/settings/event";
		}

		eventService.updateEventTitle(event, newTitle);
		attributes.addFlashAttribute("message", "Updated event title.");
		return "redirect:/event/" + event.getEncodedPath() + "/settings/event";
	}

}
