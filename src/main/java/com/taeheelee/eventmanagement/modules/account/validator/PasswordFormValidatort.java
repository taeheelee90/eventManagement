package com.taeheelee.eventmanagement.modules.account.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.taeheelee.eventmanagement.modules.account.form.PasswordForm;

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
