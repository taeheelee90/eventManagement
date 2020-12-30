package com.taeheelee.eventmanagement.modules.account.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.taeheelee.eventmanagement.modules.account.AccountRepository;
import com.taeheelee.eventmanagement.modules.account.form.SignUpForm;

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
	            errors.rejectValue("nickname", "invalid.nickname", new Object[]{signUpForm.getNickname()}, "Nickname is already in use.");
	        }
	}
	
	

}
