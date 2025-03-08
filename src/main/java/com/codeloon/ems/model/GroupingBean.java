package com.codeloon.ems.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupingBean {
    private Long id;
    private String username;
    private String eventType;
    private String eventName;
    private int totalParticipants;
    private Integer numberOfGroups;
    private String groupingMethod;
    private String filePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}