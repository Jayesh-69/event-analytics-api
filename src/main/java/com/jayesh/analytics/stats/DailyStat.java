package com.jayesh.analytics.stats;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

/**
 * One row per (date, page): pre-aggregated by {@link RollupJob}.
 */
@Entity
@Table(name = "daily_stats")
public class DailyStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    @Column(name = "page_url", nullable = false, length = 512)
    private String pageUrl;

    @Column(nullable = false)
    private long views;

    @Column(name = "unique_users", nullable = false)
    private long uniqueUsers;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected DailyStat() {
        // required by JPA
    }

    public Long getId() { return id; }
    public LocalDate getStatDate() { return statDate; }
    public String getPageUrl() { return pageUrl; }
    public long getViews() { return views; }
    public long getUniqueUsers() { return uniqueUsers; }
    public Instant getUpdatedAt() { return updatedAt; }
}
