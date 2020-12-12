package com.taeheelee.eventmanagement.settings.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class NicknameForm {

	@NotBlank(message = "Please enter nickname.")
	@Length(min = 3, max = 20, message = "Please enter 3-20 characters.")
	@Pattern(regexp="^[a-zA-Z0-9_-]{3,20}$", message="Only alphabets, - and _ are allowed.")
	private String nickname;
}
