package com.taeheelee.eventmanagement.domain;

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

	private LocalDateTime recruitingUpdatedDateTime;

	private boolean recruiting;

	private boolean published;

	private boolean closed;

	private boolean useBanner;

	public void addManager(Account account) {
		this.managers.add(account);
	}

	public boolean isJoinable(UserAccount userAccount) {
		Account account = userAccount.getAccount();
		return this.isPublished() && this.isRecruiting() && !this.members.contains(account)
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

}
