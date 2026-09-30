package com.jayesh.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EventAnalyticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventAnalyticsApplication.class, args);
    }
}
