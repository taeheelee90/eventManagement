package com.taeheelee.eventmanagement.modules.event;

import java.util.List;

public interface EventRepositoryExtension {
	
	List<Event> findByKeyword(String keyword);
}
