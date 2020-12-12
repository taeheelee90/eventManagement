package com.taeheelee.eventmanagement.settings;

import com.taeheelee.eventmanagement.domain.Account;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Notifications {

	private boolean eventCreatedByEmail;
	
	private boolean eventCreatedByWeb;
	
	private boolean eventEnrollmentResultByEmail;
	
	private boolean eventEnrollmentResultByWeb;
	
	private boolean eventUpdatedByEmail;
	
	private boolean eventUpdatedByWeb;
	
	public Notifications (Account account) {
		this.eventCreatedByEmail = account.isEventCreatedByEmail();
		this.eventCreatedByWeb = account.isEventCreatedByWeb();
		this.eventEnrollmentResultByEmail = account.isEventEnrollmentByEmail();
		this.eventEnrollmentResultByWeb = account.isEventEnrollmentByWeb();
		this.eventUpdatedByEmail = account.isEventUpdateByEmail();
		this.eventUpdatedByWeb = account.isEventUpdateByWeb();
	}
	
	
	
	
}
