package com.taeheelee.eventmanagement.modules.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.taeheelee.eventmanagement.modules.account.Account;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	long countByAccountAndChecked(Account account, boolean checked);

}
