package com.jayesh.analytics.stats;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class StatsService {

    private final DailyStatRepository dailyStatRepository;

    public StatsService(DailyStatRepository dailyStatRepository) {
        this.dailyStatRepository = dailyStatRepository;
    }

    @Transactional(readOnly = true)
    public PageViewsResponse pageViews(LocalDate from, LocalDate to) {
        validateRange(from, to);
        List<PageViewsResponse.Day> days = dailyStatRepository.viewsPerDay(from, to).stream()
                .map(r -> new PageViewsResponse.Day(r.getDate(), r.getViews()))
                .toList();
        long total = days.stream().mapToLong(PageViewsResponse.Day::views).sum();
        return new PageViewsResponse(from, to, total, days);
    }

    @Transactional(readOnly = true)
    public List<TopPageResponse> topPages(LocalDate from, LocalDate to, int limit) {
        validateRange(from, to);
        return dailyStatRepository.topPages(from, to, limit).stream()
                .map(r -> new TopPageResponse(r.getPageUrl(), r.getViews(), r.getUniqueUsers()))
                .toList();
    }

    /** Aggregates the given UTC days (inclusive) into daily_stats. Returns rows written. */
    @Transactional
    public int rollup(LocalDate fromDay, LocalDate toDay) {
        validateRange(fromDay, toDay);
        return dailyStatRepository.rollup(
                fromDay.atStartOfDay().toInstant(ZoneOffset.UTC),
                toDay.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC));
    }

    private static void validateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'from' must be on or before 'to'");
        }
    }
}
