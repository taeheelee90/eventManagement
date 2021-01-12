package com.taeheelee.eventmanagement.modules.account;

import java.util.Set;

import com.querydsl.core.types.Predicate;
import com.taeheelee.eventmanagement.modules.tag.Tag;


public class AccountPredicates {
	
	public static Predicate findByTags(Set<Tag> tags) {
		QAccount account = QAccount.account;
		return account.tags.any().in(tags);
	}

}
