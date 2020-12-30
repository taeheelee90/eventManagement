package com.taeheelee.eventmanagement.modules.activity;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.taeheelee.eventmanagement.modules.event.Event;

@Transactional(readOnly = true)
public interface ActivityRepository extends JpaRepository <Activity, Long> {

	@EntityGraph(value = "Activity.withEnrollments", type = EntityGraph.EntityGraphType.LOAD)
	List<Activity> findByEventOrderByStartDateTime(Event event);

}
