package com.taeheelee.eventmanagement.modules.account.form;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class Profile {

	@Length (max = 35, message ="Too long text.")
	private String bio;
	
	@Length (max = 50, message ="Too long text.")
	private String url;
	
	@Length (max = 50, message ="Too long text.")
	private String occupation;
	
	@Length (max = 50, message ="Too long text.")
	private String location;
	
	private String profileImage;

}
