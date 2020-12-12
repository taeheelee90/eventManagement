package com.taeheelee.eventmanagement.settings;

import lombok.Data;

@Data
public class Notifications {

	private boolean eventCreatedByEmail;
	
	private boolean eventCreatedByWeb;
	
	private boolean eventEnrollmentResultByEmail;
	
	private boolean eventEnrollmentResultByWeb;
	
	private boolean eventUpdatedByEmail;
	
	private boolean eventUpdatedByWeb;
	
}
