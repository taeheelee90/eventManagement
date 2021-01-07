package com.taeheelee.eventmanagement.modules.event;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.Table;

import com.taeheelee.eventmanagement.modules.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

//@NamedEntityGraph(name = "Registration.withEvent", attributeNodes = {
//	@NamedAttributeNode(value = "activity", subgraph = "event") }, subgraphs = @NamedSubgraph(name = "event", attributeNodes = @NamedAttributeNode("event")))
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Table(name = "registration")
public class Registration {
	
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	private Event event;

	@ManyToOne
	private Account account;

	private LocalDateTime enrolledAt;

	private boolean accepted;

	private boolean attended;
}
