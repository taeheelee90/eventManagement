package com.taeheelee.eventmanagement.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.taeheelee.eventmanagement.domain.Tag;

@Transactional (readOnly = true)
public interface TagRepository extends JpaRepository <Tag, Long> {

	Tag findByTitle(String title);

	

}
