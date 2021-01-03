package com.taeheelee.eventmanagement.modules.activity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.taeheelee.eventmanagement.modules.account.Account;
import com.taeheelee.eventmanagement.modules.account.UserAccount;
import com.taeheelee.eventmanagement.modules.event.Event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@NamedEntityGraph(name = "Activity.withEnrollments", attributeNodes = @NamedAttributeNode("enrollments"))
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Table(name = "activity")
public class Activity {

	@Id
	@GeneratedValue
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

	@Column
	private Integer limitOfEnrollments;

	@OneToMany(mappedBy = "activity")
	private List<Enrollment> enrollments = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	private ActivityType activityType;

	public boolean isEnrollableFor(UserAccount userAccount) {
        return isNotClosed() && !isAttended(userAccount) && !isAlreadyEnrolled(userAccount);
	}

	public boolean isAttended(UserAccount userAccount) {
		Account account = userAccount.getAccount();
		for (Enrollment e : this.enrollments) {
			if (e.getAccount().equals(account) && e.isAttended()) {
				return true;
			}

		}
		return false;
	}

	public boolean isDisenrollableFor(UserAccount userAccount) {
		return isNotClosed() && !isAttended(userAccount) && isAlreadyEnrolled(userAccount);
	}

	public boolean isAlreadyEnrolled(UserAccount userAccount) {
		Account account = userAccount.getAccount();
		for (Enrollment e : this.enrollments) {
			if (e.getAccount().equals(account)) {
				return true;
			}
		}
		return false;
	}

	public boolean isNotClosed() {
		return this.endEnrollmentDateTime.isAfter(LocalDateTime.now());
	}

	public int numberOfRemainSpots() {
		return this.limitOfEnrollments - (int) this.enrollments.stream().filter(Enrollment::isAccepted).count();
	}

	public long getNumberOfAcceptedEnrollments() {
		return this.enrollments.stream().filter(Enrollment::isAccepted).count();
	}

	public void addEnrollment(Enrollment enrollment) {
		this.enrollments.add(enrollment);
		enrollment.setActivity(this);
	}

	public void removeEnrollment(Enrollment enrollment) {
		this.enrollments.remove(enrollment);
		enrollment.setActivity(null);
	}

	public boolean isAbleToAcceptWaitingEnrollment() {
		return this.activityType == ActivityType.FCFS
				&& this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments();
	}

	public boolean canAccept(Enrollment enrollment) {
		return this.activityType == ActivityType.COMFIRMATIVE && this.enrollments.contains(enrollment)
				&& this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments() && !enrollment.isAttended()
				&& !enrollment.isAccepted();
	}

	public boolean canReject(Enrollment enrollment) {
		return this.activityType == ActivityType.COMFIRMATIVE && this.enrollments.contains(enrollment)
				&& !enrollment.isAttended() && enrollment.isAccepted();
	}

	public List<Enrollment> getWaitingList() {
		return this.enrollments.stream().filter(enrollment -> !enrollment.isAccepted()).collect(Collectors.toList());
	}

	public void acceptWaitingList() {
		if (this.isAbleToAcceptWaitingEnrollment()) {
			List<Enrollment> waitingList = getWaitingList();
			int numberToAccept = (int) Math.min(this.limitOfEnrollments - this.getNumberOfAcceptedEnrollments(),
					waitingList.size());
			waitingList.subList(0, numberToAccept).forEach(e -> e.setAccepted(true));
		}
	}

	public void acceptNextWaitingEnrollment() {
		if (this.isAbleToAcceptWaitingEnrollment()) {
			Enrollment enrollmentToAccept = this.getTheFirstWaitingEnrollment();
			if (enrollmentToAccept != null) {
				enrollmentToAccept.setAccepted(true);
			}
		}
	}

	private Enrollment getTheFirstWaitingEnrollment() {
		for (Enrollment e : this.enrollments) {
			if (!e.isAccepted()) {
				return e;
			}
		}
		return null;
	}

	public void accept(Enrollment enrollment) {
		if (this.activityType == ActivityType.COMFIRMATIVE
				&& this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments()) {
			enrollment.setAccepted(true);
		}
	}

	public void reject(Enrollment enrollment) {
		if (this.activityType == ActivityType.COMFIRMATIVE) {
			enrollment.setAccepted(false);
		}
	}
}
