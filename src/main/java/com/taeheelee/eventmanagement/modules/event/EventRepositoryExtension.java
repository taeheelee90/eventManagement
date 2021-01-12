package com.taeheelee.eventmanagement.modules.event;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.taeheelee.eventmanagement.modules.tag.Tag;


@Transactional(readOnly = true)
public interface EventRepositoryExtension {
	
	Page<Event> findByKeyword(String keyword, Pageable pageable);
	
	List<Event> findByAccount(Set<Tag> tags);
}
