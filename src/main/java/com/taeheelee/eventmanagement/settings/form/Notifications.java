package com.taeheelee.eventmanagement.settings.form;

import lombok.Data;

@Data
public class Notifications {

private boolean eventCreatedByEmail;
	
	private boolean eventCreatedByWeb;
	
	private boolean eventEnrollmentByEmail;
	
	private boolean eventEnrollmentByWeb;
	
	private boolean eventUpdateByEmail;
	
	private boolean eventUpdateByWeb;
	
}
