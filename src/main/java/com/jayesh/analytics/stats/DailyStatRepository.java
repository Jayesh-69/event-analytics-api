package com.jayesh.analytics.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface DailyStatRepository extends JpaRepository<DailyStat, Long> {

    /**
     * Re-aggregates PAGE_VIEW events in [from, to) into daily_stats.
     * Idempotent: ON CONFLICT overwrites the row, so re-running never double counts.
     */
    @Modifying
    @Query(value = """
            INSERT INTO daily_stats (stat_date, page_url, views, unique_users, updated_at)
            SELECT (occurred_at AT TIME ZONE 'UTC')::date,
                   page_url,
                   COUNT(*),
                   COUNT(DISTINCT user_id),
                   now()
            FROM events
            WHERE event_type = 'PAGE_VIEW'
              AND occurred_at >= :from
              AND occurred_at <  :to
            GROUP BY 1, 2
            ON CONFLICT (stat_date, page_url)
            DO UPDATE SET views        = EXCLUDED.views,
                          unique_users = EXCLUDED.unique_users,
                          updated_at   = now()
            """, nativeQuery = true)
    int rollup(@Param("from") Instant from, @Param("to") Instant to);

    @Query(value = """
            SELECT stat_date AS date, SUM(views) AS views, SUM(unique_users) AS uniqueUsers
            FROM daily_stats
            WHERE stat_date BETWEEN :from AND :to
            GROUP BY stat_date
            ORDER BY stat_date
            """, nativeQuery = true)
    List<DailyViewsRow> viewsPerDay(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query(value = """
            SELECT page_url AS pageUrl, SUM(views) AS views, SUM(unique_users) AS uniqueUsers
            FROM daily_stats
            WHERE stat_date BETWEEN :from AND :to
            GROUP BY page_url
            ORDER BY SUM(views) DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<TopPageRow> topPages(@Param("from") LocalDate from, @Param("to") LocalDate to, @Param("limit") int limit);

    /** Projection for {@link #viewsPerDay}. */
    interface DailyViewsRow {
        LocalDate getDate();
        long getViews();
        long getUniqueUsers();
    }

    /** Projection for {@link #topPages}. */
    interface TopPageRow {
        String getPageUrl();
        long getViews();
        long getUniqueUsers();
    }
}
