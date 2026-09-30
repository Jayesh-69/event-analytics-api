package com.jayesh.analytics.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByUserIdOrderByOccurredAtDesc(String userId, Pageable pageable);
}
