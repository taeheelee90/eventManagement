package com.taeheelee.eventmanagement.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.taeheelee.eventmanagement.domain.Account;
import com.taeheelee.eventmanagement.domain.Activity;
import com.taeheelee.eventmanagement.domain.Enrollment;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

	boolean existsByActivityAndAccount(Activity activity, Account account);

	Enrollment findByActivityAndAccount(Activity activity, Account account);

	
}
