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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.taeheelee.eventmanagement.modules.account.Account;
import com.taeheelee.eventmanagement.modules.account.UserAccount;
import com.taeheelee.eventmanagement.modules.tag.Tag;
import com.taeheelee.eventmanagement.modules.zone.Zone;

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

	@ManyToMany
	private Set<Tag> tags = new HashSet<>();

	@ManyToMany
	private Set<Zone> zones = new HashSet<>();

	private LocalDateTime publishedDateTime; // Publish event

	private LocalDateTime closedDateTime; // Close event

	private LocalDateTime registrationUpdatedDateTime; // will delete

	//@Column(nullable = false)
	private LocalDateTime startRegistrationDateTime;

	@Column(nullable = false)
	private LocalDateTime endRegistrationDateTime;

	@Column(nullable = false)
	private LocalDateTime eventStartDateTime;

	@Column(nullable = false)
	private LocalDateTime eventEndDateTime;

	@Column
	private Integer limitOfRegistrations;

	private boolean registration;

	private boolean published;

	private boolean closed;

	private int memberCount;

	@OneToMany(mappedBy = "event")
	private List<Registration> registrations = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	private RegistrationType registrationType;

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
		// URLEncoder.encode(this.path, StandardCharsets.US_ASCII);

		return URLEncoder.encode(this.path);
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

	public boolean canRegister(UserAccount userAccount) {
		return isNotClosed() && !isAttended(userAccount) && !isAlreadyRegistered(userAccount);
	}

	public boolean isAttended(UserAccount userAccount) {
		Account account = userAccount.getAccount();
		for (Registration e : this.registrations) {
			if (e.getAccount().equals(account) && e.isAttended()) {
				return true;
			}

		}
		return false;
	}

	public boolean cannotRegister(UserAccount userAccount) {
		return isNotClosed() && !isAttended(userAccount) && isAlreadyRegistered(userAccount);
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
		return this.startRegistrationDateTime.isAfter(LocalDateTime.now());
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

	public boolean isAbleToAcceptWaitingRegistration() {
		return this.registrationType == RegistrationType.FCFS
				&& this.limitOfRegistrations > this.getNumberOfAcceptedRegistrations();
	}

	public boolean canAccept(Registration registration) {
		return this.registrationType == RegistrationType.COMFIRMATIVE && this.registrations.contains(registration)
				&& this.limitOfRegistrations > this.getNumberOfAcceptedRegistrations() && !registration.isAttended()
				&& !registration.isAccepted();
	}

	public boolean canReject(Registration registration) {
		return this.registrationType == RegistrationType.COMFIRMATIVE && this.registrations.contains(registration)
				&& !registration.isAttended() && registration.isAccepted();
	}

	public List<Registration> getWaitingList() {
		return this.registrations.stream().filter(registration -> !registration.isAccepted())
				.collect(Collectors.toList());
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

	private Registration getTheFirstWaitingRegistration() {
		for (Registration e : this.registrations) {
			if (!e.isAccepted()) {
				return e;
			}
		}
		return null;
	}

	public void accept(Registration registration) {
		if (this.registrationType == RegistrationType.COMFIRMATIVE
				&& this.limitOfRegistrations > this.getNumberOfAcceptedRegistrations()) {
			registration.setAccepted(true);
		}
	}

	public void reject(Registration registration) {
		if (this.registrationType == RegistrationType.COMFIRMATIVE) {
			registration.setAccepted(false);
		}
	}

}
