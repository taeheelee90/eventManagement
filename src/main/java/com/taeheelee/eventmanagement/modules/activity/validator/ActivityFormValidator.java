package com.taeheelee.eventmanagement.modules.activity.validator;

import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.taeheelee.eventmanagement.modules.activity.Activity;
import com.taeheelee.eventmanagement.modules.activity.form.ActivityForm;

@Component
public class ActivityFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return ActivityForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ActivityForm activityForm = (ActivityForm) target;
		
		if (invalidEndEnrollmentDateTime(activityForm)) {
			errors.rejectValue("endEnrollmentDateTime", "wrong.datetime", "Please check due date of enrollment.");
		}
		
		if(invalidEndDateTime(activityForm)) {
			errors.rejectValue("endDateTime", "wrong.datetime", "Please check end date and time.");
		}
		
		if (invalidStartDateTime(activityForm)) {
			errors.rejectValue("startDateTime", "wrong.datetime", "Please check start date adn time.");
		}
		
	}

	private boolean invalidStartDateTime(ActivityForm activityForm) {
		return activityForm.getStartDateTime().isBefore(activityForm.getEndEnrollmentDateTime());
	}
	

	private boolean invalidEndDateTime(ActivityForm activityForm) {
		return activityForm.getEndEnrollmentDateTime().isBefore(LocalDateTime.now());
	}

	private boolean invalidEndEnrollmentDateTime(ActivityForm activityForm) {
		LocalDateTime endDateTime = activityForm.getEndDateTime();
		return endDateTime.isBefore(activityForm.getStartDateTime()) || endDateTime.isBefore(activityForm.getEndEnrollmentDateTime());
				
	}

	public void validateUpdateForm(@Valid ActivityForm activityForm, Activity activity, Errors errors) {
		
		if(activityForm.getLimitOfEnrollments() < activity.getNumberOfAcceptedEnrollments()) {
			errors.rejectValue("limitOfEnrollments", "wrong.value", "Can not set lower number than the number of accepted enrollments.");
		}
		
		
	}
	

}
