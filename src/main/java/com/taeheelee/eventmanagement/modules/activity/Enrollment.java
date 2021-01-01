package com.taeheelee.eventmanagement.modules.activity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;

import com.taeheelee.eventmanagement.modules.account.Account;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@NamedEntityGraph(name = "Enrollment.withActivityAndEvent", attributeNodes = {
		@NamedAttributeNode(value = "activity", subgraph = "event") }, subgraphs = @NamedSubgraph(name = "event", attributeNodes = @NamedAttributeNode("event")))
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Enrollment {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	private Activity activity;

	@ManyToOne
	private Account account;

	private LocalDateTime enrolledAt;

	private boolean accepted;

	private boolean attended;
}
