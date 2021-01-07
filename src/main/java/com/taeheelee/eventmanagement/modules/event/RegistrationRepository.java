package com.taeheelee.eventmanagement.modules.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.taeheelee.eventmanagement.modules.account.Account;


@Transactional(readOnly = true)
public interface RegistrationRepository extends JpaRepository<Registration, Long>{
	
	boolean existsByEventAndAccount(Event event, Account account);

	Registration findByEventAndAccount(Event event, Account account);


}
