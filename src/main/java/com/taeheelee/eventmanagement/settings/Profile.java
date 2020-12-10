package com.taeheelee.eventmanagement.settings;

import org.hibernate.validator.constraints.Length;

import com.taeheelee.eventmanagement.domain.Account;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Profile {

	@Length (max = 35)
	private String bio;
	
	@Length (max = 50)
	private String url;
	
	@Length (max = 50)
	private String occupation;
	
	@Length (max = 50)
	private String location;
	
	private String profileImage;
	
	public Profile (Account account) {
		this.bio = account.getBio();
		this.url = account.getUrl();
		this.occupation = account.getOccupation();
		this.location = account.getLocation();
		this.profileImage = account.getProfileImage();
	}
}
