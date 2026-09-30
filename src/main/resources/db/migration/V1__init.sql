-- Raw events: append-only, one row per tracked interaction
CREATE TABLE events (
    id           BIGSERIAL    PRIMARY KEY,
    user_id      VARCHAR(64)  NOT NULL,
    event_type   VARCHAR(32)  NOT NULL,
    page_url     VARCHAR(512) NOT NULL,
    occurred_at  TIMESTAMPTZ  NOT NULL,
    received_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_events_occurred_at ON events (occurred_at);
CREATE INDEX idx_events_user_time   ON events (user_id, occurred_at DESC);

-- Pre-aggregated daily page stats, filled by the hourly rollup job
CREATE TABLE daily_stats (
    id            BIGSERIAL    PRIMARY KEY,
    stat_date     DATE         NOT NULL,
    page_url      VARCHAR(512) NOT NULL,
    views         BIGINT       NOT NULL,
    unique_users  BIGINT       NOT NULL,
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_daily_stats_date_page UNIQUE (stat_date, page_url)
);
