package com.taeheelee.eventmanagement.Account;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class SignUpForm {

	@NotBlank
	@Length(min = 3, max = 20)
	//@Pattern(regexp = "^[a-zA-Z!@#$%^&*()-_?]{3,20}$")
	private String nickname;
	
	@Email
	@NotBlank
	private String email;
	
	@NotBlank
	@Length(min = 8, max = 50)
	private String password;
}
