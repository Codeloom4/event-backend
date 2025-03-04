package com.codeloon.ems.repository;

import com.codeloon.ems.dto.GalleryDto;
import com.codeloon.ems.entity.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Integer> {

    // Find images by event type
    List<Gallery> findByEventType(String eventType);

    // Find all images with event description
    @Query("SELECT new com.codeloon.ems.dto.GalleryDto(g.id, g.eventType, e.description, g.groupName, g.imagePath, g.createdAt) " +
            "FROM Gallery g JOIN Event e ON g.eventType = e.eventType")
    List<GalleryDto> findAllWithEventDescription();

    // Find all images by group name
    List<Gallery> findByGroupName(String groupName);

    // Delete all images by group name
    @Modifying
    @Transactional
    @Query("DELETE FROM Gallery g WHERE g.groupName = :groupName")
    void deleteByGroupName(String groupName);
}