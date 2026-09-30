package com.jayesh.analytics.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * Incoming event payload. occurredAt is optional; server time is used if missing.
 */
public record EventRequest(
        @NotBlank @Size(max = 64) String userId,
        @NotNull EventType eventType,
        @NotBlank @Size(max = 512) String pageUrl,
        @PastOrPresent Instant occurredAt
) {
}
