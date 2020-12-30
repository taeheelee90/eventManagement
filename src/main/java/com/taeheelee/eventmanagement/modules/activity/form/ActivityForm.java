package com.taeheelee.eventmanagement.modules.activity.form;

import java.time.LocalDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import com.taeheelee.eventmanagement.modules.activity.ActivityType;

import lombok.Data;

@Data
public class ActivityForm {

	@NotBlank
	@Length(max = 50)
	private String title;
	
	private String description;
	
	private ActivityType activityType = ActivityType.FCFS;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime endEnrollmentDateTime;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime startDateTime;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime endDateTime;
	
	@Min(2)
	private Integer limitOfEnrollments = 2;
}
