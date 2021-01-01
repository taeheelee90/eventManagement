package com.taeheelee.eventmanagement.modules.event.eventPublisher;

import org.springframework.scheduling.annotation.Async;

import com.taeheelee.eventmanagement.modules.event.Event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Async
@RequiredArgsConstructor
public class EventCreated {

	private final Event event;
}
