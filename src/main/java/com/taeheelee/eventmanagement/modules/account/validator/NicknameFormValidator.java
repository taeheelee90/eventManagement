package com.taeheelee.eventmanagement.modules.account.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.taeheelee.eventmanagement.modules.account.Account;
import com.taeheelee.eventmanagement.modules.account.AccountRepository;
import com.taeheelee.eventmanagement.modules.account.form.NicknameForm;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NicknameFormValidator implements Validator {

	private final AccountRepository accountRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return NicknameForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		
		NicknameForm nicknameForm = (NicknameForm) target;
		
		Account byNickname = accountRepository.findByNickname(nicknameForm.getNickname());
		
		if(byNickname != null) {
			errors.rejectValue("nickname","wrong.value", "Nickname is already in use.");
		}
		
		
	}

}
