package com.taeheelee.eventmanagement.Account;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

	private final AccountRepository accountRepository;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(SignUpForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		SignUpForm signUpForm = (SignUpForm) target;
		
		 if (accountRepository.existsByEmail(signUpForm.getEmail())) {
	            errors.rejectValue("email", "invalid.email", new Object[]{signUpForm.getEmail()}, "Email is laready in use.");
	        }

	        if (accountRepository.existsByNickname(signUpForm.getNickname())) {
	            errors.rejectValue("nickname", "invalid.nickname", new Object[]{signUpForm.getEmail()}, "Nickname is already in use.");
	        }
	}
	
	

}
