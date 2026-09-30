# Event Analytics API

A Spring Boot REST service that ingests user interaction events (page views, clicks, scrolls), stores them in PostgreSQL, and serves aggregate analytics from an hourly pre-aggregated rollup table.

## Stack

Java 17 · Spring Boot 3 (Web, Data JPA, Validation, Scheduling) · PostgreSQL 16 · Flyway · Docker Compose

## Architecture

```
Client ──POST /api/events──► EventController ──► EventService ──► events (raw, append-only)
                                                                        │
                                           RollupJob (@Scheduled, hourly)
                                                                        ▼
Dashboard ◄──GET /api/stats/*── StatsController ◄── StatsService ◄── daily_stats (pre-aggregated)
```

- **Ingest path** is write-only and cheap: validate → insert one row.
- **Read path** never scans raw events for page stats. It reads `daily_stats`, which has one row per (day, page).
- **Rollup** re-aggregates today + yesterday every hour with `INSERT ... ON CONFLICT DO UPDATE`, so it is idempotent and picks up late-arriving events.

## Run locally

```bash
docker compose up -d          # starts Postgres on :5432
mvn spring-boot:run
```

Flyway creates the tables on startup.

## API

### Ingest an event
```bash
curl -X POST localhost:8080/api/events \
  -H 'Content-Type: application/json' \
  -d '{"userId":"u1","eventType":"PAGE_VIEW","pageUrl":"/pricing"}'
```
`eventType`: `PAGE_VIEW` | `CLICK` | `SCROLL`. `occurredAt` (ISO-8601) is optional and defaults to server time.

### Trigger rollup manually (demo / backfill)
```bash
curl -X POST 'localhost:8080/api/admin/rollup'                              # today
curl -X POST 'localhost:8080/api/admin/rollup?from=2026-09-01&to=2026-09-30' # backfill
```

### Page views per day
```bash
curl 'localhost:8080/api/stats/page-views?from=2026-09-01&to=2026-09-30'
```

### Top pages
```bash
curl 'localhost:8080/api/stats/top-pages?from=2026-09-01&to=2026-09-30&limit=5'
```

### A user's recent activity (raw events)
```bash
curl 'localhost:8080/api/stats/users/u1/events?limit=20'
```

## Tests

```bash
mvn test
```
