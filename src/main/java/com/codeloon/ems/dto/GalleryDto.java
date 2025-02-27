package com.codeloon.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GalleryDto {
    private Integer id;
    private String eventType;
    private String groupName;
    private String imageUrl; // Add this field
    private LocalDateTime createdAt;
}