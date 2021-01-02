package com.taeheelee.eventmanagement.modules.activity.eventPublisher;

import com.taeheelee.eventmanagement.modules.activity.Enrollment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @RequiredArgsConstructor
public abstract class EnrollmentEvent {

	
	protected final Enrollment enrollment;
	
	protected final String message;

}
