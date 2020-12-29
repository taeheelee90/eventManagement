package com.taeheelee.eventmanagement.activity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taeheelee.eventmanagement.Account.CurrentUser;
import com.taeheelee.eventmanagement.activity.form.ActivityForm;
import com.taeheelee.eventmanagement.activity.validator.ActivityFormValidator;
import com.taeheelee.eventmanagement.domain.Account;
import com.taeheelee.eventmanagement.domain.Activity;
import com.taeheelee.eventmanagement.domain.Event;
import com.taeheelee.eventmanagement.event.EventRepository;
import com.taeheelee.eventmanagement.event.EventService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/event/{path}")
@RequiredArgsConstructor
public class ActivityController {

	private final ActivityRepository activityRepository;
	private final EventRepository eventRepository;
	private final EventService eventService;
	private final ActivityService activityService;
	private final ModelMapper modelMapper;
	private final ActivityFormValidator activityFormValidator;

	@InitBinder("activityForm")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(activityFormValidator);
	}

	@GetMapping("/new-activity")
	public String newActivityForm(@CurrentUser Account account, @PathVariable String path, Model model) {
		Event event = eventService.getEventToUpdateStatus(account, path);
		model.addAttribute(event);
		model.addAttribute(account);
		model.addAttribute(new ActivityForm());
		return "activity/form";
	}

	@PostMapping("/new-activity")
	public String newActivitySubmit(@CurrentUser Account account, @PathVariable String path,
			@Valid ActivityForm activityForm, Errors errors, Model model) {
		Event event = eventService.getEventToUpdateStatus(account, path);
		if (errors.hasErrors()) {
			model.addAttribute(account);
			model.addAttribute(event);
			return "activity/form";
		}

		Activity activity = activityService.createEvent(modelMapper.map(activityForm, Activity.class), event, account);
		return "redirect:/event/" + event.getEncodedPath() + "/activities/" + activity.getId();

	}

	@GetMapping("/activities/{id}")
	public String getEvent(@CurrentUser Account account, @PathVariable String path,
			@PathVariable("id") Activity activity, Model model) {
		model.addAttribute(account);
		model.addAttribute(activity);
		model.addAttribute(eventRepository.findEventWithManagersByPath(path));
		return "activity/view";
	}

	@GetMapping("/activities")
	public String viewEventActivities(@CurrentUser Account account, @PathVariable String path, Model model) {
		Event event = eventService.getEvent(path);
		model.addAttribute(account);
		model.addAttribute(event);

		List<Activity> activities = activityRepository.findByEventOrderByStartDateTime(event);
		List<Activity> newActivities = new ArrayList<>();
		List<Activity> oldActivities = new ArrayList<>();
		activities.forEach(a -> {
			if (a.getEndDateTime().isBefore(LocalDateTime.now())) {
				oldActivities.add(a);
			} else {
				newActivities.add(a);
			}
		});

		model.addAttribute("oldActivities", oldActivities);
		model.addAttribute("newActivities", newActivities);

		return "event/activities";
	}

	@GetMapping("/activities/{id}/edit")
	public String updateActivity(@CurrentUser Account account, @PathVariable String path,
			@PathVariable("id") Activity activity, Model model) {
		Event event = eventService.getEventToUpdate(account, path);
		model.addAttribute(event);
		model.addAttribute(account);
		model.addAttribute(activity);
		model.addAttribute(modelMapper.map(activity, ActivityForm.class));
		return "activity/update-form";
	}

	@PostMapping("/activities/{id}/edit")
	public String updateActivitySubmit(@CurrentUser Account account, @PathVariable String path,
			@PathVariable("id") Activity activity, @Valid ActivityForm activityForm, Errors errors, Model model) {

		Event event = eventService.getEventToUpdate(account, path);
		activityForm.setActivityType(activity.getActivityType());
		activityFormValidator.validateUpdateForm(activityForm, activity, errors);
	
		
		if(errors.hasErrors()) {
			model.addAttribute(account);
			model.addAttribute(event);
			model.addAttribute(activity);
			
			return "activity/update-form";
		}
		
		activityService.updateActivity(activity, activityForm);
		
		return "redirect:/event/" + event.getEncodedPath() + "/activities/" + activity.getId();
		
	}

}
