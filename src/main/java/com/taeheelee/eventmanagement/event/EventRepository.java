package com.taeheelee.eventmanagement.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.taeheelee.eventmanagement.domain.Event;

@Transactional (readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {

	boolean existsByPath(String path);



}
