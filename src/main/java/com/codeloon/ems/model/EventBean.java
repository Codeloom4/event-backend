package com.codeloon.ems.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventBean {
    private String eventType; // Primary Key
    private String description;
    private LocalDateTime createdAt;
}