package com.taeheelee.eventmanagement.domain;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;

import com.taeheelee.eventmanagement.Account.UserAccount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NamedEntityGraph(name = "Event.withAll", attributeNodes = { @NamedAttributeNode("tags"), @NamedAttributeNode("zones"),
		@NamedAttributeNode("managers"), @NamedAttributeNode("members") })
@NamedEntityGraph(name = "Event.withTagsAndManagers", attributeNodes = { @NamedAttributeNode("tags"),
		@NamedAttributeNode("managers") })
@NamedEntityGraph(name = "Event.withZonesAndManagers", attributeNodes = { @NamedAttributeNode("zones"),
		@NamedAttributeNode("managers") })
@NamedEntityGraph(name = "Event.withManagers", attributeNodes = { @NamedAttributeNode("managers") })
@NamedEntityGraph(name = "Event.withMembers", attributeNodes = { @NamedAttributeNode("members") })
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToMany
	private Set<Account> managers = new HashSet<>();

	@ManyToMany
	private Set<Account> members = new HashSet<>();

	@Column(unique = true)
	private String path;

	private String title;

	private String shortDescription;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	private String fullDescription;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	private String image;

	@ManyToMany
	private Set<Tag> tags = new HashSet<>();

	@ManyToMany
	private Set<Zone> zones = new HashSet<>();

	private LocalDateTime publishedDateTime;

	private LocalDateTime closedDateTime;

	private LocalDateTime registrationUpdatedDateTime;

	private boolean registration;

	private boolean published;

	private boolean closed;

	private int memberCount;

	public void addManager(Account account) {
		this.managers.add(account);
	}

	public boolean isJoinable(UserAccount userAccount) {
		Account account = userAccount.getAccount();
		return this.isPublished() && this.isRegistration() && !this.members.contains(account)
				&& !this.managers.contains(account);
	}

	public boolean isMember(UserAccount userAccount) {
		return this.members.contains(userAccount.getAccount());
	}

	public boolean isManager(UserAccount userAccount) {
		return this.managers.contains(userAccount.getAccount());
	}

	public boolean isMagedBy(Account account) {
		return this.getManagers().contains(account);
	}

	public void publish() {
		if (!this.closed && !this.published) {
			this.published = true;
			this.publishedDateTime = LocalDateTime.now();
		} else {
			throw new RuntimeException("Can not publish this event.");
		}

	}

	public void close() {
		if (this.published && !this.closed) {
			this.closed = true;
			this.closedDateTime = LocalDateTime.now();
		} else {
			throw new RuntimeException("Can not close this event.");
		}
	}

	public boolean canUpdateRegistration() {
		return this.published && this.registrationUpdatedDateTime == null
				|| this.registrationUpdatedDateTime.isBefore(LocalDateTime.now().minusHours(1));
	}

	public void startRegistration() {
		if (canUpdateRegistration()) {
			this.registration = true;
			this.registrationUpdatedDateTime = LocalDateTime.now();
		} else {
			throw new RuntimeException("Can not start registration.");
		}
	}

	public void stopRegistration() {
		if (canUpdateRegistration()) {
			this.registration = false;
			this.registrationUpdatedDateTime = LocalDateTime.now();
		} else {
			throw new RuntimeException("Can not stop registration now.");
		}
	}

	public String getEncodedPath() {

		return URLEncoder.encode(this.path, StandardCharsets.UTF_8);
	}

	public boolean isRemovable() {
		return !this.published;
	}

	public void addMember(Account account) {
		this.getMembers().add(account);
		this.memberCount++;
	}

	public void removeMember(Account account) {
		this.getMembers().remove(account);
		this.memberCount--;		
	}

}
