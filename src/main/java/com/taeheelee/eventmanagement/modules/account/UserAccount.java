package com.taeheelee.eventmanagement.modules.account;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

@Getter
public class UserAccount extends User {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Account account;
	
	public UserAccount(Account account) {
		super(account.getNickname(), account.getPassword(), Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
		this.account = account;
	}

	
	



}
