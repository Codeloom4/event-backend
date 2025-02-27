package com.codeloon.ems.service;

import com.codeloon.ems.dto.GalleryDto;
import com.codeloon.ems.util.ResponseBean;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GalleryService {
    ResponseBean uploadImages(String eventType, String groupName, List<MultipartFile> images);
    ResponseBean getImagesByEventType(String eventType);
    ResponseBean deleteImage(Integer id);
    ResponseBean updateGroupName(Integer id, String groupName);
    ResponseBean updateGroup(String groupName, List<MultipartFile> images);
    ResponseBean deleteGroup(String groupName);
    ResponseBean getAllImages();
}