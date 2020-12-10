package com.taeheelee.eventmanagement.Account;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taeheelee.eventmanagement.domain.Account;
import com.taeheelee.eventmanagement.settings.Profile;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

	private final AccountRepository accountRepository;
	private final JavaMailSender javaMailSender;
	private final PasswordEncoder passWordEncoder;

	public Account processNewAccount(@Valid SignUpForm signUpForm) {
		Account newAccount = saveNewAccount(signUpForm);
		newAccount.generateEmailCheckToken();
		sendSignUpConfirmEmail(newAccount);

		return newAccount;
	}

	private Account saveNewAccount(@Valid SignUpForm signUpForm) {
		Account account = Account.builder().email(signUpForm.getEmail()).nickname(signUpForm.getNickname())
				.password(passWordEncoder.encode(signUpForm.getPassword())).eventCreatedByWeb(true)
				.eventEnrollmentByWeb(true).eventUpdateByWeb(true).build();

		Account newAccount = accountRepository.save(account);
		return newAccount;
	}

	public void sendSignUpConfirmEmail(Account newAccount) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(newAccount.getEmail());
		mailMessage.setSubject("[Verification] Welcome to Event Managment.");
		mailMessage.setText(
				"/check-email-token?token=" + newAccount.getEmailCheckToken() + "&email=" + newAccount.getEmail());

		javaMailSender.send(mailMessage);
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
		account.setBio(profile.getBio());
		account.setUrl(profile.getUrl());
		account.setOccupation(profile.getOccupation());
		account.setLocation(profile.getLocation());
		account.setProfileImage(profile.getProfileImage());
		accountRepository.save(account);
	}

	public Account getAccount(String nickname) {
		Account account = accountRepository.findByNickname(nickname);
		if (account == null) {
			throw new IllegalArgumentException("Can not find " + nickname);
		}
		return account;
	}
}
