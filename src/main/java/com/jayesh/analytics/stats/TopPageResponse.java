package com.jayesh.analytics.stats;

public record TopPageResponse(
        String pageUrl,
        long views,
        long uniqueUsers
) {
}
