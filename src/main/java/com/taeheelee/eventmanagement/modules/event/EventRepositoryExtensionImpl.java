package com.taeheelee.eventmanagement.modules.event;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.taeheelee.eventmanagement.modules.tag.QTag;
import com.taeheelee.eventmanagement.modules.tag.Tag;
import com.taeheelee.eventmanagement.modules.zone.QZone;
import com.taeheelee.eventmanagement.modules.zone.Zone;

public class EventRepositoryExtensionImpl extends QuerydslRepositorySupport implements EventRepositoryExtension {

	public EventRepositoryExtensionImpl() {
		super(Event.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Page<Event> findByKeyword(String keyword, Pageable pageable) {
		QEvent event = QEvent.event;
		JPQLQuery<Event> query = from(event)
				.where(event.published.isTrue().and(event.title.containsIgnoreCase(keyword))
						.or(event.tags.any().title.containsIgnoreCase(keyword))
						.or(event.zones.any().city.containsIgnoreCase(keyword)))
				.leftJoin(event.tags, QTag.tag).fetchJoin().leftJoin(event.zones, QZone.zone).fetchJoin().distinct();

		JPQLQuery<Event> pageableQuery = getQuerydsl().applyPagination(pageable, query);
		QueryResults<Event> fetchResults = pageableQuery.fetchResults();
		return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
	}

	@Override
	public List<Event> findByAccount(Set<Tag> tags, Set<Zone> zones) {
		QEvent event = QEvent.event;
		JPQLQuery<Event> query = from(event)
				.where(event.published.isTrue().and(event.closed.isFalse()).and(event.tags.any().in(tags))
						.and(event.zones.any().in(zones)))
				.leftJoin(event.tags, QTag.tag).fetchJoin().leftJoin(event.zones, QZone.zone).fetchJoin()
				.orderBy(event.publishedDateTime.desc()).distinct().limit(9);

		return query.fetch();

	}

}
