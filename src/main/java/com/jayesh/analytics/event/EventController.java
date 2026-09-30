package com.jayesh.analytics.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse ingest(@Valid @RequestBody EventRequest request) {
        return EventResponse.from(eventService.ingest(request));
    }

    @GetMapping("/stats/users/{userId}/events")
    public List<EventResponse> userEvents(@PathVariable String userId,
                                          @RequestParam(defaultValue = "50") @Min(1) @Max(500) int limit) {
        return eventService.recentForUser(userId, limit).stream()
                .map(EventResponse::from)
                .toList();
    }
}
