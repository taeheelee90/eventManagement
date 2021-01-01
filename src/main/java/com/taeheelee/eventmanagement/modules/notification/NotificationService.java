package com.taeheelee.eventmanagement.modules.notification;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

	@Autowired private final NotificationRepository notificationRepository;
	
	public void markAsOld(List<Notification> notifications) {
		notifications.forEach(n -> n.setChecked(true));
		notificationRepository.saveAll(notifications);
		
	}

}
