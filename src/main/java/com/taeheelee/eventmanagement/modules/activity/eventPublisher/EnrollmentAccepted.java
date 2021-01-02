package com.taeheelee.eventmanagement.modules.activity.eventPublisher;

import com.taeheelee.eventmanagement.modules.activity.Enrollment;

public class EnrollmentAccepted extends EnrollmentEvent {

	public EnrollmentAccepted(Enrollment enrollment, String message) {
		super(enrollment, "Enrollment for activity is accepted. ");
	}
	

}
