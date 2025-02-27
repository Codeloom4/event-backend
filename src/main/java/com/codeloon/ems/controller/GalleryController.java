package com.codeloon.ems.controller;

import com.codeloon.ems.dto.GalleryDto;
import com.codeloon.ems.service.GalleryService;
import com.codeloon.ems.util.ResponseBean;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/ems/gallery")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<ResponseBean> uploadImages(
            @RequestParam String eventType,
            @RequestParam String groupName,
            @RequestParam List<MultipartFile> images) {
        ResponseBean responseBean = galleryService.uploadImages(eventType, groupName, images);
        return new ResponseEntity<>(responseBean, HttpStatus.CREATED);
    }

    @GetMapping("/event/{eventType}")
    public ResponseEntity<ResponseBean> getImagesByEventType(@PathVariable String eventType) {
        ResponseBean responseBean = galleryService.getImagesByEventType(eventType);
        return new ResponseEntity<>(responseBean, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<ResponseBean> deleteImage(@PathVariable Integer id) {
        ResponseBean responseBean = galleryService.deleteImage(id);
        return new ResponseEntity<>(responseBean, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<ResponseBean> updateGroupName(
            @PathVariable Integer id,
            @RequestParam String groupName) {
        ResponseBean responseBean = galleryService.updateGroupName(id, groupName);
        return new ResponseEntity<>(responseBean, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseBean> getAllImages() {
        ResponseBean responseBean = galleryService.getAllImages();
        return new ResponseEntity<>(responseBean, HttpStatus.OK);
    }
}