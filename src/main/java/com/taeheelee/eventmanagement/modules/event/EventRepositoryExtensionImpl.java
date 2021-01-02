package com.taeheelee.eventmanagement.modules.event;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.jpa.JPQLQuery;

public class EventRepositoryExtensionImpl extends QuerydslRepositorySupport implements EventRepositoryExtension {

	public EventRepositoryExtensionImpl() {
		super(Event.class);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public List<Event> findByKeyword(String keyword){
		QEvent event = QEvent.event;
		JPQLQuery<Event> query = from(event).where(event.published.isTrue()
								.and(event.title.containsIgnoreCase(keyword))
								.or(event.tags.any().title.containsIgnoreCase(keyword))
								.or(event.zones.any().city.containsIgnoreCase(keyword)));
		return query.fetch();
	}

}
