package com.codeloon.ems.service;

import com.codeloon.ems.util.ResponseBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {
    ResponseBean uploadImages(String packageId, MultipartFile[] files);

    ResponseBean getImages(String packageId);

    void deleteImages(String packageId);
}
