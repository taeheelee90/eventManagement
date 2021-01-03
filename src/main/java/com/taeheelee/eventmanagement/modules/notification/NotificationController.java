package com.taeheelee.eventmanagement.modules.notification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.taeheelee.eventmanagement.modules.account.Account;
import com.taeheelee.eventmanagement.modules.account.CurrentUser;


import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class NotificationController {

	@Autowired private final NotificationRepository notificationRepository;
	@Autowired private final NotificationService notificationService;
	
	@GetMapping("/notifications")
	public String showNotifications(@CurrentUser Account account, Model model) {
		List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedAtDesc(account, false);
		long numberOfChecked = notificationRepository.countByAccountAndChecked(account, true);
		categorizeNotifications(model, notifications, numberOfChecked, notifications.size());
		model.addAttribute("isNew", true);
		notificationService.markAsOld(notifications);
		return "notification/view";
	}
	
	@GetMapping("/notifications/old")
	public String showOldNotifications(@CurrentUser Account account, Model model) {
		List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedAtDesc(account, true);
		long numberOfChecked = notificationRepository.countByAccountAndChecked(account, false);
		categorizeNotifications(model, notifications, numberOfChecked, notifications.size());
		model.addAttribute("isNew", false);
		return "notification/view";
	}
	
	@DeleteMapping("/notifications")
	public String deleteNotification(@CurrentUser Account account) {
		notificationRepository.deleteByAccountAndChecked(account, true);
		return "redirect:/notifications";
	}


	private void categorizeNotifications(Model model, List<Notification> notifications, long numberOfChecked,
			long numberOfNotChecked) {
		List<Notification> createdEventNotifications = new ArrayList<>();
		List<Notification> activityEnrollmentNotifications = new ArrayList<>();
		List<Notification> updatedEventNotifications = new ArrayList<>();
		
		for(Notification n : notifications) {
			switch(n.getNotificationType()) {
				case EVENT_CREATED: createdEventNotifications.add(n); break;
				case ACTIVITY_ENROLLMENT: activityEnrollmentNotifications.add(n); break;
				case EVENT_UPDATED: updatedEventNotifications.add(n); break;
			}
		}
		
		model.addAttribute("numberOfNotChecked", numberOfNotChecked);
		model.addAttribute("numberOfChecked", numberOfChecked);
		model.addAttribute("notifications", notifications);
		model.addAttribute("createdEventNotifications", createdEventNotifications);
		model.addAttribute("activityEnrollmentNotifications", activityEnrollmentNotifications);
		model.addAttribute("updatedEventNotifications", updatedEventNotifications);
		
	}
}
