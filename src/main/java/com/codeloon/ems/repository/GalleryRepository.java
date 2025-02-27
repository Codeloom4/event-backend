package com.codeloon.ems.repository;

import com.codeloon.ems.entity.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Integer> {
    List<Gallery> findByEventType(String eventType); // Find images by event type
}