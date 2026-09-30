package com.jayesh.analytics.stats;

import java.time.LocalDate;
import java.util.List;

public record PageViewsResponse(
        LocalDate from,
        LocalDate to,
        long totalViews,
        List<Day> days
) {
    public record Day(LocalDate date, long views) {
    }
}
