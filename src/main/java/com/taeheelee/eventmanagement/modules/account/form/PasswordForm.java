package com.taeheelee.eventmanagement.modules.account.form;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class PasswordForm {

	@Length (min=8, max=50, message="8-50 characters.")
	private String newPassword;
	
	@Length (min=8, max=50, message="8-50 characters.")
	private String newPasswordConfirm;
}
