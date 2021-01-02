package com.taeheelee.eventmanagement.modules.activity.eventPublisher;

import com.taeheelee.eventmanagement.modules.activity.Enrollment;

public class EnrollmentRejected extends EnrollmentEvent {

	public EnrollmentRejected(Enrollment enrollment, String message) {
		super(enrollment, "Enrollment for activity is rejected.");
	}

}
