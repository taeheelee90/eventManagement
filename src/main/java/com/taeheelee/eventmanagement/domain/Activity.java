package com.taeheelee.eventmanagement.domain;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Activity {

	@Id @GeneratedValue
	private Long id;
	
	@ManyToOne
	private Event event;
	
	@ManyToOne
	private Account createdBy;
	
	@Column(nullable = false)
	private String title;
	
	@Lob
	private String description;
	
	@Column(nullable = false)
	private LocalDateTime createdDateTime;
	
	@Column(nullable = false)
	private LocalDateTime endEnrollmentDateTime;
	
	@Column(nullable = false)
	private LocalDateTime startDateTime;
	
	@Column(nullable = false)
	private LocalDateTime endDateTime;
	
	@Column(nullable = true)
	private Integer limitOfEnrollments;
	
	@OneToMany(mappedBy = "activity")
	private List<Enrollment> enrollments;

	@Enumerated (EnumType.STRING)
	private ActivityType activityType;
}
