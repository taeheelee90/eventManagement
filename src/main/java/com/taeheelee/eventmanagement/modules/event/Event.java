package com.taeheelee.eventmanagement.modules.event;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.taeheelee.eventmanagement.modules.account.Account;
import com.taeheelee.eventmanagement.modules.account.UserAccount;
import com.taeheelee.eventmanagement.modules.tag.Tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "event")
public class Event {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	private Account manager;

	@ManyToMany
	private Set<Account> members = new HashSet<>();

	@Column(unique = true)
	private String path;

	private String title;

	private String shortDescription;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	private String fullDescription;

	@ManyToMany
	private Set<Tag> tags = new HashSet<>();

	@Column
	private Integer limitOfRegistrations;

	private boolean registration; // Check if it is registration period

	private LocalDateTime endRegistrationDateTime;

	private LocalDateTime eventStartDateTime;

	private LocalDateTime eventEndDateTime;

	private boolean closed;

	private LocalDateTime closedDateTime;

	@OneToMany(mappedBy = "event")
	private List<Registration> registrations = new ArrayList<>();

	/*
	 * Processing event
	 */
	public String getEncodedPath() {

		return URLEncoder.encode(this.path);
	}

	public void addMember(Account account, Registration registration) {
		if (this.limitOfRegistrations > this.getNumberOfAcceptedRegistrations()) {
			this.getMembers().add(account);
			this.addRegistration(registration);
		}

		else {
			throw new RuntimeException("Cannot register.");
		}

	}

	public void removeMember(Account account, Registration registration) {
		this.getMembers().remove(account);
		this.removeregistration(registration);

	}

	public void close() {
		if (this.registration && !this.closed) {
			this.registration = false;
			this.closed = true;
			this.closedDateTime = LocalDateTime.now();
		} else {
			throw new RuntimeException("Cannot close this event.");
		}

	}

	/*
	 * Processing registration
	 * 
	 */

	public List<Registration> getWaitingList() {
		return this.registrations.stream().filter(registration -> !registration.isAccepted())
				.collect(Collectors.toList());
	}

	public int numberOfRemainSpots() {
		return this.limitOfRegistrations - (int) this.registrations.stream().filter(Registration::isAccepted).count();
	}

	public long getNumberOfAcceptedRegistrations() {
		return this.registrations.stream().filter(Registration::isAccepted).count();
	}

	public void addRegistration(Registration registration) {
		this.registrations.add(registration);
		registration.setEvent(this);
	}

	public void removeregistration(Registration registration) {
		this.registrations.remove(registration);
		registration.setEvent(null);
	}

	private Registration getTheFirstWaitingRegistration() {
		for (Registration e : this.registrations) {
			if (!e.isAccepted()) {
				return e;
			}
		}
		return null;
	}

	public void acceptWaitingList() {
		if (this.isAbleToAcceptWaitingRegistration()) {
			List<Registration> waitingList = getWaitingList();
			int numberToAccept = (int) Math.min(this.limitOfRegistrations - this.getNumberOfAcceptedRegistrations(),
					waitingList.size());
			waitingList.subList(0, numberToAccept).forEach(e -> e.setAccepted(true));
		}
	}

	public void acceptNextWaitingregistration() {
		if (this.isAbleToAcceptWaitingRegistration()) {
			Registration registrationToAccept = this.getTheFirstWaitingRegistration();
			if (registrationToAccept != null) {
				registrationToAccept.setAccepted(true);
			}
		}
	}

	/*
	 * Check if..
	 */

	public boolean isMember(UserAccount userAccount) {
		return this.members.contains(userAccount.getAccount());
	}

	public boolean isManager(UserAccount userAccount) {
		return this.manager.equals(userAccount.getAccount());
	}

	public boolean isMagedBy(Account account) {
		return this.getManager().equals(account);
	}

	public boolean isJoinable(UserAccount userAccount) {
		Account account = userAccount.getAccount();
		return this.isRegistration() && !this.members.contains(account) && !this.manager.equals(account);
	}

	public boolean canRegister(UserAccount userAccount) {
		return isNotClosed() && !isAlreadyRegistered(userAccount);
	}

	public boolean cannotRegister(UserAccount userAccount) {
		return isNotClosed() && isAlreadyRegistered(userAccount);
	}

	public boolean isAlreadyRegistered(UserAccount userAccount) {
		Account account = userAccount.getAccount();
		for (Registration e : this.registrations) {
			if (e.getAccount().equals(account)) {
				return true;
			}
		}
		return false;
	}

	public boolean isNotClosed() {
		return this.endRegistrationDateTime.isAfter(LocalDateTime.now());
	}

	public boolean isAbleToAcceptWaitingRegistration() {
		return this.limitOfRegistrations > this.getNumberOfAcceptedRegistrations();
	}

}
