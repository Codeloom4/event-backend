package com.codeloon.ems.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventBean {

    private Long id;

    private String eventType;

    private String description;

    private LocalDateTime createdAt;
}

