package com.taeheelee.eventmanagement.modules.event;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {

	boolean existsByPath(String path);

	@EntityGraph(value = "Event.withAll", type = EntityGraph.EntityGraphType.LOAD)
	Event findByPath(String path);

	@EntityGraph(value = "Event.withTagsAndManagers", type = EntityGraph.EntityGraphType.FETCH)
	Event findEventWithTagsByPath(String path);

	@EntityGraph(value = "Event.withZonesAndManagers", type = EntityGraph.EntityGraphType.FETCH)
	Event findEventWithZonesByPath(String path);

	@EntityGraph(value = "Event.withManagers", type = EntityGraph.EntityGraphType.FETCH)
	Event findEventWithManagersByPath(String path);

	@EntityGraph(value = "Event.withMembers", type = EntityGraph.EntityGraphType.FETCH)
	Event findEventWithMembersByPath(String path);

	Event findEventOnlyByPath(String path);

}