package com.taeheelee.eventmanagement.modules.event;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryExtension {



	boolean existsByPath(String path);

	@EntityGraph(attributePaths = {"tags", "zones", "managers", "members"}, type = EntityGraph.EntityGraphType.LOAD)
	Event findByPath(String path);

	@EntityGraph(attributePaths = {"tags", "managers"})
	Event findEventWithTagsByPath(String path);

	@EntityGraph(attributePaths = {"zones", "managers"})
	Event findEventWithZonesByPath(String path);

	@EntityGraph(attributePaths = {"managers"})
	Event findEventWithManagersByPath(String path);

	@EntityGraph(attributePaths = {"members"})
	Event findEventWithMembersByPath(String path);

	Event findEventOnlyByPath(String path);
	
	@EntityGraph(attributePaths = {"zones", "tags"})
	Event findEventWithTagsAndZonesById(Long id);

	@EntityGraph(attributePaths = {"members", "managers"})
	Event findEventWithManagersAndMembersById(Long id);

	List<Event> findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(boolean published, boolean closed);

	

}
