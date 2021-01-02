package com.taeheelee.eventmanagement.modules.event.eventPublisher;

import com.taeheelee.eventmanagement.modules.event.Event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventUpdated {

	private final Event event;
	
	private final String message;
}
