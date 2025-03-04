package com.codeloon.ems.service;

import com.codeloon.ems.dto.GalleryDto;
import com.codeloon.ems.entity.Gallery;
import com.codeloon.ems.repository.GalleryRepository;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GalleryServiceImpl implements GalleryService {

    private final GalleryRepository galleryRepository;

    @Value("${image.upload.directory}")
    private String uploadDirectory; // Configured in application.yml

    @Override
    public ResponseBean uploadImages(String eventType, String groupName, List<MultipartFile> images) {
        ResponseBean responseBean = new ResponseBean();
        try {
            List<GalleryDto> uploadedImages = new ArrayList<>();

            for (MultipartFile file : images) {
                // Generate a unique file name
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDirectory, groupName, fileName);
                String relativeImagePath = groupName + "/" + fileName;

                // Create the directory if it doesn't exist
                Files.createDirectories(filePath.getParent());

                // Save the file to the server
                Files.copy(file.getInputStream(), filePath);

                // Save the image metadata to the database
                Gallery gallery = Gallery.builder()
                        .eventType(eventType)
                        .groupName(groupName)
                        .imagePath(relativeImagePath)
                        .createdAt(LocalDateTime.now())
                        .build();
                galleryRepository.save(gallery);

                // Add to the response
                GalleryDto galleryDto = new GalleryDto();
                BeanUtils.copyProperties(gallery, galleryDto);
                uploadedImages.add(galleryDto);
            }

            responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
            responseBean.setResponseMsg("Images uploaded successfully.");
            responseBean.setContent(uploadedImages);
        } catch (IOException ex) {
            log.error("Error occurred while uploading images: {}", ex.getMessage());
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Failed to upload images.");
        }
        return responseBean;
    }

    @Override
    public ResponseBean getImagesByEventType(String eventType) {
        ResponseBean responseBean = new ResponseBean();
        try {
            List<Gallery> galleries = galleryRepository.findByEventType(eventType);
            responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
            responseBean.setResponseMsg("Images retrieved successfully.");
            responseBean.setContent(galleries);
        } catch (Exception ex) {
            log.error("Error occurred while retrieving images: {}", ex.getMessage());
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Failed to retrieve images.");
        }
        return responseBean;
    }

    @Override
    public ResponseBean deleteImage(Integer id) {
        ResponseBean responseBean = new ResponseBean();
        try {
            galleryRepository.deleteById(id);
            responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
            responseBean.setResponseMsg("Image deleted successfully.");
        } catch (Exception ex) {
            log.error("Error occurred while deleting image: {}", ex.getMessage());
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Failed to delete image.");
        }
        return responseBean;
    }

    @Override
    public ResponseBean updateGroupName(Integer id, String groupName) {
        ResponseBean responseBean = new ResponseBean();
        try {
            Gallery gallery = galleryRepository.findById(id).orElseThrow(() -> new RuntimeException("Image not found"));
            gallery.setGroupName(groupName);
            galleryRepository.save(gallery);
            responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
            responseBean.setResponseMsg("Group name updated successfully.");
            responseBean.setContent(gallery);
        } catch (Exception ex) {
            log.error("Error occurred while updating group name: {}", ex.getMessage());
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Failed to update group name.");
        }
        return responseBean;
    }

    @Override
    public ResponseBean getAllImages() {
        ResponseBean responseBean = new ResponseBean();
        try {
//            List<Gallery> galleries = galleryRepository.findAll();
            List<GalleryDto> galleryDtos = galleryRepository.findAllWithEventDescription();

            // Map Gallery entities to GalleryDto objects
//            List<GalleryDto> galleryDtos = galleries.stream()
//                    .map(gallery -> GalleryDto.builder()
//                            .id(gallery.getId())
//                            .eventType(gallery.getEventType())
//                            .groupName(gallery.getGroupName())
//                            .imageUrl("http://localhost:9999/uploads/" + gallery.getImagePath()) // Construct image URL
//                            .createdAt(gallery.getCreatedAt())
//                            .build()
//                    )
//                    .collect(Collectors.toList());

            // Construct image URLs
            galleryDtos.forEach(galleryDto -> {
                galleryDto.setImageUrl("http://localhost:9999/uploads/" + galleryDto.getImageUrl());
            });

            responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
            responseBean.setResponseMsg("All images retrieved successfully.");
            responseBean.setContent(galleryDtos);
        } catch (Exception ex) {
            log.error("Error occurred while retrieving all images: {}", ex.getMessage());
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Failed to retrieve images.");
        }
        return responseBean;
    }

    @Override
    public ResponseBean deleteGroup(String groupName) {
        ResponseBean responseBean = new ResponseBean();
        try {
            List<Gallery> galleries = galleryRepository.findByGroupName(groupName);
            if (galleries.isEmpty()) {
                responseBean.setResponseCode(ResponseCode.RSP_ERROR);
                responseBean.setResponseMsg("Group not found.");
                return responseBean;
            }

            // Delete images from the server
            for (Gallery gallery : galleries) {
                Path filePath = Paths.get(uploadDirectory, gallery.getImagePath());
                Files.deleteIfExists(filePath);
            }

            // Delete images from the database
            galleryRepository.deleteByGroupName(groupName);

            responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
            responseBean.setResponseMsg("Group deleted successfully.");
        } catch (Exception ex) {
            log.error("Error occurred while deleting group: {}", ex.getMessage());
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Failed to delete group.");
        }
        return responseBean;
    }

    @Override
    public ResponseBean updateGroup(String groupName, List<MultipartFile> images) {
        ResponseBean responseBean = new ResponseBean();
        try {
            List<Gallery> existingGalleries = galleryRepository.findByGroupName(groupName);
            if (existingGalleries.isEmpty()) {
                responseBean.setResponseCode(ResponseCode.RSP_ERROR);
                responseBean.setResponseMsg("Group not found.");
                return responseBean;
            }

            // Delete existing images from the server
//            for (Gallery gallery : existingGalleries) {
//                Path filePath = Paths.get(uploadDirectory, gallery.getImagePath());
//                Files.deleteIfExists(filePath);
//            }

            // Delete existing images from the database
//            galleryRepository.deleteByGroupName(groupName);

            // Upload new images
            List<GalleryDto> uploadedImages = new ArrayList<>();
            for (MultipartFile file : images) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDirectory, groupName, fileName);
                String relativeImagePath = groupName + "/" + fileName;

                Files.createDirectories(filePath.getParent());
                Files.copy(file.getInputStream(), filePath);

                Gallery gallery = Gallery.builder()
                        .eventType(existingGalleries.get(0).getEventType()) // Use the same event type
                        .groupName(groupName)
                        .imagePath(relativeImagePath)
                        .createdAt(LocalDateTime.now())
                        .build();
                galleryRepository.save(gallery);

                GalleryDto galleryDto = new GalleryDto();
                BeanUtils.copyProperties(gallery, galleryDto);
                uploadedImages.add(galleryDto);
            }

            responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
            responseBean.setResponseMsg("Group updated successfully.");
            responseBean.setContent(uploadedImages);
        } catch (Exception ex) {
            log.error("Error occurred while updating group: {}", ex.getMessage());
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Failed to update group.");
        }
        return responseBean;
    }


}