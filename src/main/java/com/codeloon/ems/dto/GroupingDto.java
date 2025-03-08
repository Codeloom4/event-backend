package com.codeloon.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupingDto {
    private String eventType;
    private String eventName;
    private int totalParticipants;
    private Integer numberOfGroups;
    private String groupingMethod;
    private String filePath;
}