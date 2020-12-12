package com.taeheelee.eventmanagement.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@EqualsAndHashCode( of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account {
	
	@Id @GeneratedValue
	private Long id;
	
	@Column(unique = true)
	private String email;
	
	@Column(unique = true)
	private String nickname;
	
	private String password;
	
	private boolean emailVerified;
	
	private String emailCheckToken;
	
	private LocalDateTime emailCheckTokenGeneratedAt;
	
	private LocalDateTime joinedAt;
	
	private String bio;
	
	private String url;
	
	private String occupation;
	
	private String location;
	
	@Lob @Basic(fetch = FetchType.EAGER)
	private String profileImage;
	
	private boolean eventCreatedByEmail;
	
	private boolean eventCreatedByWeb = true;
	
	private boolean eventEnrollmentByEmail;
	
	private boolean eventEnrollmentByWeb = true;
	
	private boolean eventUpdateByEmail;
	
	private boolean eventUpdateByWeb = true;

	public void generateEmailCheckToken() {
		this.emailCheckToken = UUID.randomUUID().toString();
		this.emailCheckTokenGeneratedAt = LocalDateTime.now();
	}

	public void completeSignUp() {
		this.setEmailVerified(true);
		this.setJoinedAt(LocalDateTime.now());
		
	}

	public boolean isValidtoken(String token) {
		return this.emailCheckToken.equals(token);
	}

	public boolean canSendConfirmEmail() {
		return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
	}
	

}
