package com.taeheelee.eventmanagement.modules.notification;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.taeheelee.eventmanagement.modules.account.Account;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@EqualsAndHashCode(of = "id")
@Table(name = "notification")
public class Notification {

	@Id @GeneratedValue
	private Long id;
	
	private String title;
	
	private String link;
	
	private String message;
	
	private boolean checked;
	
	@ManyToOne
	private Account account;
	
	private LocalDateTime createdAt;
	
	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;
}
