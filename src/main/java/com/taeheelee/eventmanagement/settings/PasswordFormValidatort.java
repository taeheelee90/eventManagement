package com.taeheelee.eventmanagement.settings;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PasswordFormValidatort implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		
		return PasswordForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		PasswordForm passwordForm = (PasswordForm) target;
		if(!passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm())) {
			errors.rejectValue("newPassword", "wrong.value", "Confirmation Failed: Passwords are different.");
		}
		
	}

}
