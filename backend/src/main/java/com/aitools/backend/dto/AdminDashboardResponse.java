package com.aitools.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminDashboardResponse {

    private Stats stats;
    private List<Item> recent;

    @Data
    public static class Stats {
        private Long tools;
        private Long tutorials;
        private Long users;
        private Long apis;
        private Long prompts;
    }

    @Data
    public static class Item {
        private Long id;
        private String title;
        private LocalDateTime createdAt;
        private String kind; // tool | tutorial
    }
}

