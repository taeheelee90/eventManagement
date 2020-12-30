package com.taeheelee.eventmanagement.modules.tag;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@EqualsAndHashCode( of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Tag {
	
	@Id @GeneratedValue
	private Long id;
	
	@Column(unique =true, nullable=false)
	private String title;
}
