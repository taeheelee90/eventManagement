package com.taeheelee.eventmanagement.modules.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.taeheelee.eventmanagement.modules.account.Account;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

	boolean existsByActivityAndAccount(Activity activity, Account account);

	Enrollment findByActivityAndAccount(Activity activity, Account account);

	
}
