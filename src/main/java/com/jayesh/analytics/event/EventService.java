package com.jayesh.analytics.event;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public Event ingest(EventRequest request) {
        Instant occurredAt = request.occurredAt() != null ? request.occurredAt() : Instant.now();
        Event event = new Event(request.userId(), request.eventType(), request.pageUrl().trim(), occurredAt);
        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<Event> recentForUser(String userId, int limit) {
        return eventRepository.findByUserIdOrderByOccurredAtDesc(userId, PageRequest.of(0, limit));
    }
}
