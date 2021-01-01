package com.taeheelee.eventmanagement.modules.account;

import java.util.Set;

import com.querydsl.core.types.Predicate;
import com.taeheelee.eventmanagement.modules.tag.Tag;
import com.taeheelee.eventmanagement.modules.zone.Zone;

public class AccountPredicates {
	
	public static Predicate findByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
		QAccount account = QAccount.account;
		return account.zones.any().in(zones).and(account.tags.any().in(tags));
	}

}
