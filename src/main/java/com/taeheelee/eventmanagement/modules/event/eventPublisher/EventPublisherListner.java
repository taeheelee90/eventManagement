package com.taeheelee.eventmanagement.modules.event.eventPublisher;

import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.taeheelee.eventmanagement.infra.config.AppProperties;
import com.taeheelee.eventmanagement.infra.mail.EmailMessage;
import com.taeheelee.eventmanagement.infra.mail.EmailService;
import com.taeheelee.eventmanagement.modules.account.Account;
import com.taeheelee.eventmanagement.modules.account.AccountPredicates;
import com.taeheelee.eventmanagement.modules.account.AccountRepository;
import com.taeheelee.eventmanagement.modules.event.Event;
import com.taeheelee.eventmanagement.modules.event.EventRepository;
import com.taeheelee.eventmanagement.modules.notification.Notification;
import com.taeheelee.eventmanagement.modules.notification.NotificationRepository;
import com.taeheelee.eventmanagement.modules.notification.NotificationType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Async
@Slf4j
@Transactional
@RequiredArgsConstructor
public class EventPublisherListner {

	
	private final EventRepository eventRepository;
	private final AccountRepository accountRepository;
	private final NotificationRepository notificationRepository;
	private final AppProperties appProperties;
	private final TemplateEngine templateEngine;
	private final EmailService emailService;

	@EventListener
	public void listenEventCreated(EventCreated eventCreated) {
		Event event = eventRepository.findEventWithTagsAndZonesById(eventCreated.getEvent().getId());
		Iterable<Account> accounts = accountRepository
				.findAll(AccountPredicates.findByTagsAndZones(event.getTags(), event.getZones()));
		accounts.forEach(account -> {
			if (account.isEventCreatedByEmail()) {
				sendEventCreatedEmail(event, account, "New event created",
						"Event Management, '" + event.getTitle() + "'.");
			}

			if (account.isEventCreatedByWeb()) {
				sendNotification(event, account, event.getShortDescription(), NotificationType.EVENT_CREATED);
			}
		});
	}

	private void sendNotification(Event event, Account account, String msg, NotificationType type) {
		Notification notification = new Notification();
		notification.setTitle(event.getTitle());
		notification.setLink("/event/" + event.getEncodedPath());
		notification.setChecked(false);
		notification.setCreatedAt(LocalDateTime.now());
		notification.setMessage(msg);
		notification.setAccount(account);
		notification.setNotificationType(type);
		notificationRepository.save(notification);

	}

	private void sendEventCreatedEmail(Event event, Account account, String msg, String subject) {
		Context context = new Context();
		context.setVariable("nickname", account.getNickname());
		context.setVariable("link", "/event/" + event.getEncodedPath());
		context.setVariable("linkName", event.getTitle());
		context.setVariable("message", msg);
		context.setVariable("host", appProperties.getHost());
		String emailMsg = templateEngine.process("mail/simple-link", context);
		
		EmailMessage emailMessage = EmailMessage.builder()
									.subject(subject)
									.to(account.getEmail())
									.message(emailMsg)
									.build();
		
		emailService.sendEmail(emailMessage);

	}

}
