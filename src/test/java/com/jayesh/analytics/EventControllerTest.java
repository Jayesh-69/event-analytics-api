package com.jayesh.analytics;

import com.jayesh.analytics.event.Event;
import com.jayesh.analytics.event.EventController;
import com.jayesh.analytics.event.EventService;
import com.jayesh.analytics.event.EventType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EventService eventService;

    @Test
    void validEventReturns201() throws Exception {
        when(eventService.ingest(any())).thenReturn(
                new Event("u1", EventType.PAGE_VIEW, "/pricing", Instant.parse("2026-09-30T10:00:00Z")));

        mvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId":"u1","eventType":"PAGE_VIEW","pageUrl":"/pricing"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("u1"))
                .andExpect(jsonPath("$.eventType").value("PAGE_VIEW"));
    }

    @Test
    void missingFieldsReturn400() throws Exception {
        mvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"eventType":"PAGE_VIEW"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.userId").exists())
                .andExpect(jsonPath("$.fields.pageUrl").exists());

        verify(eventService, never()).ingest(any());
    }

    @Test
    void unknownEventTypeReturns400() throws Exception {
        mvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId":"u1","eventType":"BOGUS","pageUrl":"/"}
                                """))
                .andExpect(status().isBadRequest());
    }
}
