package com.taeheelee.eventmanagement.mail;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Profile("local")
@Slf4j
@Component
public class ConsoleEmailService implements EmailService {
	
	
	@Override
	public void sendEmail(EmailMessage emailMessage) {
		log.info("sent email: " + emailMessage.getMessage());
		
	}

	
}
