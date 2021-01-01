package com.taeheelee.eventmanagement.modules.activity;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taeheelee.eventmanagement.modules.account.Account;
import com.taeheelee.eventmanagement.modules.activity.form.ActivityForm;
import com.taeheelee.eventmanagement.modules.event.Event;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ActivityService {

	private final ActivityRepository activityRepository;
	private final EnrollmentRepository enrollmentRepository;
	private final ModelMapper modelMapper;

	public Activity createActivity(Activity activity, Event event, Account account) {
		activity.setCreatedBy(account);
		activity.setCreatedDateTime(LocalDateTime.now());
		activity.setEvent(event);

		return activityRepository.save(activity);
	}

	public void updateActivity(Activity activity, ActivityForm activityForm) {
		modelMapper.map(activityForm, activity);
		activity.acceptWaitingList();
	}

	public void deleteActivity(Activity activity) {
		activityRepository.delete(activity);

	}

	public void enroll(Activity activity, Account account) {
		if (!enrollmentRepository.existsByActivityAndAccount(activity, account)) {
			Enrollment enrollment = new Enrollment();
			enrollment.setEnrolledAt(LocalDateTime.now());
			enrollment.setAccepted(activity.isAbleToAcceptWaitingEnrollment());
			enrollment.setAccount(account);
			activity.addEnrollment(enrollment);
			enrollmentRepository.save(enrollment);
		}

	}

	public void disenroll(Activity activity, Account account) {
		Enrollment enrollment = enrollmentRepository.findByActivityAndAccount(activity, account);
		if (!enrollment.isAttended()) {
			activity.removeEnrollment(enrollment);
			enrollmentRepository.delete(enrollment);
			activity.acceptNextWaitingEnrollment();
		}

	}

	public void acceptEnrollment(Activity activity, Enrollment enrollment) {
		activity.accept(enrollment);

	}

	public void rejectEnrollment(Activity activity, Enrollment enrollment) {
		activity.reject(enrollment);
	}

	public void checkinEnrollment(Enrollment enrollment) {
		enrollment.setAttended(true);

	}

	public void cancelCheckinEnrollment(Enrollment enrollment) {
		enrollment.setAttended(false);

	}

}
