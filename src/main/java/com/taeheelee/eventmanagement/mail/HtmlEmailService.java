package com.taeheelee.eventmanagement.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class HtmlEmailService implements EmailService {

	private final JavaMailSender javaMailSender;

	@Override
	public void sendEmail(EmailMessage emailMessage) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		
		try {
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
			mimeMessageHelper.setTo(emailMessage.getTo());
			mimeMessageHelper.setSubject(emailMessage.getSubject());
			mimeMessageHelper.setText(emailMessage.getMessage(), false);
			javaMailSender.send(mimeMessage);
			log.info("Sent email: ", emailMessage.getMessage());
		} catch (MessagingException e) {
			log.info("Failed to send email: " + e);
		}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
		
	}
}
