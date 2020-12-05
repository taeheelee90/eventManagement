package com.taeheelee.eventmanagement.Account;

import javax.validation.Valid;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taeheelee.eventmanagement.domain.Account;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
	
	private final AccountRepository accountRepository;
	private final JavaMailSender javaMailSender;
	private final PasswordEncoder passWordEncoder;
	
	private Account saveNewAccount(@Valid SignUpForm signUpForm) {
		Account account = Account.builder()
				.email(signUpForm.getEmail())
				.nickname(signUpForm.getNickname())
				.password(passWordEncoder.encode(signUpForm.getPassword()))
				.eventCreatedByWeb(true)
				.eventEnrollmentByWeb(true)
				.eventUpdateByWeb(true)
				.build();

		Account newAccount = accountRepository.save(account);
		return newAccount;
	}

	private void sendSignUpConfirmEmail(Account newAccount) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(newAccount.getEmail());
		mailMessage.setSubject("[Verification] Welcome to Event Managment.");
		mailMessage.setText(
				"/check-email-token=xxxxx" + newAccount.getEmailCheckToken() + "&email=" + newAccount.getEmail());

		javaMailSender.send(mailMessage);
	}

	public void processNewAccount(@Valid SignUpForm signUpForm) {
		Account newAccount = saveNewAccount(signUpForm);
		newAccount.generateEmailCheckToken();
		sendSignUpConfirmEmail(newAccount);
		
	}

}
