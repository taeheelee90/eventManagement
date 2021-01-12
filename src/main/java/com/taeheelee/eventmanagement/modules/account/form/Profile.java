package com.taeheelee.eventmanagement.modules.account.form;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class Profile {

	@Length (max = 35, message ="Too long text.")
	private String bio;
	
	private String profileImage;

}
