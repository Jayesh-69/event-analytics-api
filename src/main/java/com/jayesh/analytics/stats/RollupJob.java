package com.jayesh.analytics.stats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Hourly job: re-aggregates raw events for today and the last N days into daily_stats.
 * Looking back a day catches late-arriving events; the upsert keeps it idempotent.
 */
@Component
public class RollupJob {

    private static final Logger log = LoggerFactory.getLogger(RollupJob.class);

    private final StatsService statsService;
    private final int lookbackDays;

    public RollupJob(StatsService statsService,
                     @Value("${analytics.rollup.lookback-days:1}") int lookbackDays) {
        this.statsService = statsService;
        this.lookbackDays = lookbackDays;
    }

    @Scheduled(cron = "${analytics.rollup.cron}", zone = "UTC")
    public void run() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDate from = today.minusDays(lookbackDays);
        long start = System.currentTimeMillis();
        int rows = statsService.rollup(from, today);
        log.info("Rollup {}..{} upserted {} rows in {} ms", from, today, rows, System.currentTimeMillis() - start);
    }
}
