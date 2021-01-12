package com.taeheelee.eventmanagement.modules.event;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryExtension {



	boolean existsByPath(String path);

	@EntityGraph(attributePaths = {"tags", "zones", "manager", "members"}, type = EntityGraph.EntityGraphType.LOAD)
	Event findByPath(String path);

	@EntityGraph(attributePaths = {"tags", "manager"})
	Event findEventWithTagsByPath(String path);

	@EntityGraph(attributePaths = {"zones", "manager"})
	Event findEventWithZonesByPath(String path);

	@EntityGraph(attributePaths = {"manager"})
	Event findEventWithManagerByPath(String path);

	@EntityGraph(attributePaths = {"members"})
	Event findEventWithMembersByPath(String path);

	Event findEventOnlyByPath(String path);
	
	@EntityGraph(attributePaths = {"zones", "tags"})
	Event findEventWithTagsAndZonesById(Long id);

	@EntityGraph(attributePaths = {"members", "manager"})
	Event findEventWithManagerAndMembersById(Long id);

	List<Event> findFirst9ByRegistrationOrderByEventStartDateTimeDesc(boolean registration);

	@EntityGraph(value = "Event.withRegistrations", type = EntityGraph.EntityGraphType.LOAD)
	//List<Event> findByEventOrderByStartDateTime(Event event);
	List<Event> findEventOrderByEventStartDateTime(Event event);
}
