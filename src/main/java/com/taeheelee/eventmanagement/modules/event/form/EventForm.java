package com.taeheelee.eventmanagement.modules.event.form;

import java.time.LocalDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;


import lombok.Data;

@Data
public class EventForm {
	
	@NotBlank
	@Length(min = 2, max = 20)
	private String path;

	@NotBlank
	@Length(max = 50)
	private String title;

	@NotBlank
	@Length(max = 100)
	private String shortDescription;
	
	@NotBlank
	private String fullDescription;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime endRegistrationDateTime;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime eventStartDateTime;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime eventEndDateTime;
	
	@Min(2)
	private Integer limitOfRegistrations = 2;

}
