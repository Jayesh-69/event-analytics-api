package com.jayesh.analytics.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 32)
    private EventType eventType;

    @Column(name = "page_url", nullable = false, length = 512)
    private String pageUrl;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    protected Event() {
        // required by JPA
    }

    public Event(String userId, EventType eventType, String pageUrl, Instant occurredAt) {
        this.userId = userId;
        this.eventType = eventType;
        this.pageUrl = pageUrl;
        this.occurredAt = occurredAt;
        this.receivedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public EventType getEventType() { return eventType; }
    public String getPageUrl() { return pageUrl; }
    public Instant getOccurredAt() { return occurredAt; }
    public Instant getReceivedAt() { return receivedAt; }
}
