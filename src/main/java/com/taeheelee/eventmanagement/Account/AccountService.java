package com.taeheelee.eventmanagement.Account;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.taeheelee.eventmanagement.config.AppProperties;
import com.taeheelee.eventmanagement.domain.Account;
import com.taeheelee.eventmanagement.domain.Tag;
import com.taeheelee.eventmanagement.domain.Zone;
import com.taeheelee.eventmanagement.mail.EmailMessage;
import com.taeheelee.eventmanagement.mail.EmailService;
import com.taeheelee.eventmanagement.settings.form.NicknameForm;
import com.taeheelee.eventmanagement.settings.form.Notifications;
import com.taeheelee.eventmanagement.settings.form.Profile;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

	private final AccountRepository accountRepository;
	private final EmailService emailService;
	private final PasswordEncoder passWordEncoder;
	private final ModelMapper modelMapper;
	private final TemplateEngine templateEngine;
	private final AppProperties appProperties;

	public Account processNewAccount(@Valid SignUpForm signUpForm) {
		Account newAccount = saveNewAccount(signUpForm);		
		sendSignUpConfirmEmail(newAccount);
		return newAccount;
	}

	private Account saveNewAccount(@Valid SignUpForm signUpForm) {		
		signUpForm.setPassword(passWordEncoder.encode(signUpForm.getPassword()));
		Account account = modelMapper.map(signUpForm, Account.class);
		account.generateEmailCheckToken();
		return accountRepository.save(account);
	}

	public void sendSignUpConfirmEmail(Account newAccount) {
		
		Context context = new Context();
		context.setVariable("link", "/check-email-token?token=" + newAccount.getEmailCheckToken() + "&email=" + newAccount.getEmail());
		context.setVariable("nickname", newAccount.getNickname());
		context.setVariable("linkName", "Verification");
		context.setVariable("message", "Please click the link to complete sign up to Event Management.");
		context.setVariable("host", appProperties.getHost());
		
		String message = templateEngine.process("mail/simple-link", context);
		
		
		EmailMessage emailMessage = EmailMessage.builder()
									.to(newAccount.getEmail())
									.subject("[Verification] Welcome to Event Managment.")
									.message(message)
									.build();
		
		emailService.sendEmail(emailMessage);
	}

	public void login(Account account) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(new UserAccount(account),
				account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
		SecurityContextHolder.getContext().setAuthentication(token);
	}

	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {

		Account account = accountRepository.findByEmail(emailOrNickname);

		if (account == null) {
			account = accountRepository.findByNickname(emailOrNickname);
		}

		if (account == null) {
			throw new UsernameNotFoundException(emailOrNickname);
		}

		return new UserAccount(account);
	}

	public void completeSignUp(Account account) {
		account.completeSignUp();
		login(account);

	}

	
	public void updateProfile(Account account, Profile profile) {
		modelMapper.map(profile, account);
		accountRepository.save(account);
	}

	public Account getAccount(String nickname) {
		Account account = accountRepository.findByNickname(nickname);
		if (account == null) {
			throw new IllegalArgumentException("Can not find " + nickname);
		}
		return account;
	}

	public void updatePassword(Account account, String newPassword) {
		account.setPassword(passWordEncoder.encode(newPassword));
		accountRepository.save(account);
		
	}

	public void updateNotifications(Account account, @Valid Notifications notifications) {
		modelMapper.map(notifications, account);
		accountRepository.save(account);
	
	}

	public void updateNickname(Account account, @Valid NicknameForm nicknameForm) {
		modelMapper.map(nicknameForm, account);
		accountRepository.save(account);
		login(account);
	}

	public void sendLoginLink(Account account) {
		
		Context context = new Context();
		context.setVariable("link", "/login-by-email?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
		context.setVariable("nickname", account.getNickname());
		context.setVariable("linkName", "Login to Event Management");
		context.setVariable("message", "Please click the link to login to Event Management Application.");
		context.setVariable("host", appProperties.getHost());
		
		String message = templateEngine.process("mail/simple-link", context);
		
		
		EmailMessage emailMessage = EmailMessage.builder()
									.to(account.getEmail())
									.subject("Event Management: Login Link")
									.message(message)
									.build();
		
		emailService.sendEmail(emailMessage);
	}

	public void addTag(Account account, Tag tag) {
		Optional<Account> byId = accountRepository.findById(account.getId());
		byId.ifPresent(a -> a.getTags().add(tag));
		
	}

	public Set<Tag> getTags(Account account) {
		Optional<Account> byId = accountRepository.findById(account.getId());
		return byId.orElseThrow().getTags();
		
	}

	public void removeTag(Account account, Tag tag) {
		Optional<Account> byId = accountRepository.findById(account.getId());
		byId.ifPresent(a -> a.getTags().remove(tag));
		
	}

	public Set<Zone> getZones(Account account) {
		Optional<Account> byId = accountRepository.findById(account.getId());
		return byId.orElseThrow().getZones();
	}

	public void addZone(Account account, Zone zone) {
		Optional<Account> byId = accountRepository.findById(account.getId());
		byId.ifPresent(a -> a.getZones().add(zone));
		
	}

	public void removeZone(Account account, Zone zone) {
		Optional<Account> byId = accountRepository.findById(account.getId());
		byId.ifPresent(a -> a.getZones().remove(zone));
		
	}
}
