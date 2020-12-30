package com.taeheelee.eventmanagement.modules.event.form;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

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

}
