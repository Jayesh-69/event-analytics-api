package com.jayesh.analytics.event;

import java.time.Instant;

public record EventResponse(
        Long id,
        String userId,
        EventType eventType,
        String pageUrl,
        Instant occurredAt
) {
    public static EventResponse from(Event e) {
        return new EventResponse(e.getId(), e.getUserId(), e.getEventType(), e.getPageUrl(), e.getOccurredAt());
    }
}
