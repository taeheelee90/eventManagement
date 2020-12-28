package com.taeheelee.eventmanagement.event;

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
import com.taeheelee.eventmanagement.Account.CurrentUser;
import com.taeheelee.eventmanagement.domain.Account;
import com.taeheelee.eventmanagement.domain.Event;
import com.taeheelee.eventmanagement.domain.Tag;
import com.taeheelee.eventmanagement.domain.Zone;
import com.taeheelee.eventmanagement.event.Form.EventDescriptionForm;
import com.taeheelee.eventmanagement.settings.form.TagForm;
import com.taeheelee.eventmanagement.settings.form.ZoneForm;
import com.taeheelee.eventmanagement.tag.TagRepository;
import com.taeheelee.eventmanagement.tag.TagService;
import com.taeheelee.eventmanagement.zone.ZoneRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/event/{path}/settings")
@RequiredArgsConstructor
public class EventSettingController {

	private final TagRepository tagRepository;
	private final ZoneRepository zoneRepository;
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
		return "redirect:/event/" + getPath(path) + "/settings/description";

	}

	private String getPath(String path) {
		return URLEncoder.encode(path, StandardCharsets.UTF_8);
	}

	/*@GetMapping("/banner")
	public String eventBannerForm(@CurrentUser Account account, @PathVariable String path, Model model) {
		Event event = eventService.getEventToUpdate(account, path);
		model.addAttribute(account);
		model.addAttribute(event);
		return "event/settings/banner";
	}

	@PostMapping("/banner")
	public String eventBannerSubmit(@CurrentUser Account account, @PathVariable String path, String image,
			RedirectAttributes attributes) {
		Event event = eventService.getEventToUpdate(account, path);
		eventService.updateEventBanner(event, image);
		attributes.addFlashAttribute("message", "Updated Event Banner Image.");
		return "redirect:/event/" + getPath(path) + "/settings/banner";
	}

	@PostMapping("/banner/enable")
	public String enableEventBanner(@CurrentUser Account account, @PathVariable String path) {
		Event event = eventService.getEventToUpdate(account, path);
		eventService.enableEventBanner(event);
		return "redirect:/event/" + getPath(path) + "/settings/banner";
	}

	@PostMapping("/banner/disable")
	public String disableEventBanner(@CurrentUser Account account, @PathVariable String path) {
		Event event = eventService.getEventToUpdate(account, path);
		eventService.disableEventBanner(event);
		return "redirect:/event/" + getPath(path) + "/settings/banner";
	}*/

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

	@GetMapping("/zones")
	public String eventZonesForm(@CurrentUser Account account, @PathVariable String path, Model model)
			throws JsonProcessingException {
		Event event = eventService.getEventToUpdate(account, path);
		model.addAttribute(account);
		model.addAttribute(event);
		model.addAttribute("zones", event.getZones().stream().map(Zone::toString).collect(Collectors.toList()));
		List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
		model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));

		return "event/settings/zones";
	}

	@PostMapping("/zones/add")
	@ResponseBody
	public ResponseEntity addZone(@CurrentUser Account account, @PathVariable String path,
			@RequestBody ZoneForm zoneForm) {
		Event event = eventService.getEventToUpdateZone(account, path);
		Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());

		if (zone == null) {
			return ResponseEntity.badRequest().build();
		}

		eventService.addZone(event, zone);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/zones/remove")
	@ResponseBody
	public ResponseEntity removeZone(@CurrentUser Account account, @PathVariable String path,
			@RequestBody ZoneForm zoneForm) {

		Event event = eventService.getEventToUpdateZone(account, path);
		Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
		if(zone == null) {
			return ResponseEntity.badRequest().build();
		}
		
		eventService.removeZone(event, zone);
		return ResponseEntity.ok().build();
	}

}
