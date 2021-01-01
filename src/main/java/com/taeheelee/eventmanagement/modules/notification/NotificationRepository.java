package com.taeheelee.eventmanagement.modules.notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.taeheelee.eventmanagement.modules.account.Account;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	long countByAccountAndChecked(Account account, boolean checked);

	@Transactional
	List<Notification> findByAccountAndCheckedOrderByCreatedAtDesc(Account account, boolean checked);

	@Transactional
	void deleteByAccountAndChecked(Account account, boolean checked);

}
