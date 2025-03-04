package com.codeloon.ems.service;

import com.codeloon.ems.entity.PackageSlide;
import com.codeloon.ems.repository.PackageSlideRepository;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
class ImageUploadServiceImpl implements ImageUploadService {


    @Value("${file.upload.windows}")
    private String windowsUploadPath;

    @Value("${file.upload.linux}")
    private String linuxUploadPath;

    private final PackageSlideRepository packageSlideRepository;

    public ImageUploadServiceImpl(PackageSlideRepository packageSlideRepository) {
        this.packageSlideRepository = packageSlideRepository;
    }

    @Override
    public ResponseBean uploadImages(String packageId, MultipartFile[] files) {
        ResponseBean responseBean = new ResponseBean();
        String msg = null;
        String code = ResponseCode.RSP_ERROR;
        List<PackageSlide> uploadedFiles = new ArrayList<>();
        try {

            if (isEmpty(packageId)) {
                msg = "Package Id is mandatory";
            } else if (isEmpty(packageId)) {
                msg = "Package Id is Invalid";
            } else {
                for (MultipartFile file : files) {
                    String fileName = this.generateFileName(packageId, file);
                    String filePath = uploadFile(file, packageId, fileName);
                    PackageSlide slide = saveToDatabase(packageId, fileName, filePath);
                    uploadedFiles.add(slide);
                }
                msg = "Images uploaded successfully";
                code = ResponseCode.RSP_SUCCESS;
            }

        } catch (Exception ex) {
            log.error("Error occurred while retrieving system user: {}", ex.getMessage(), ex);
            msg = "Error occurred while retrieving system user.";
        }

        responseBean.setResponseMsg(msg);
        responseBean.setResponseCode(code);
        responseBean.setContent(uploadedFiles);
        return responseBean;
    }

    @Override
    public ResponseBean getImages(String packageId) {
        ResponseBean responseBean = new ResponseBean();
        List<PackageSlide> images = new ArrayList<>();
        String msg;
        String code = ResponseCode.RSP_ERROR;

        if (isEmpty(packageId)) {
            msg = "Package Id is mandatory";
        } else {
            try {
                images = packageSlideRepository.findByPackageId(packageId);
                if (images.isEmpty()) {
                    msg = "No images found for the given Package ID";
                } else {
                    msg = "Images retrieved successfully";
                    code = ResponseCode.RSP_SUCCESS;
                }
            } catch (Exception ex) {
                log.error("Error occurred while retrieving images: {}", ex.getMessage(), ex);
                msg = "Error occurred while retrieving images.";
            }
        }

        responseBean.setResponseMsg(msg);
        responseBean.setResponseCode(code);
        responseBean.setContent(images);
        return responseBean;
    }

    public void deleteImages(String packageId) {
        List<PackageSlide> images = new ArrayList<>();
        if (isEmpty(packageId)) {
            log.warn("Package Id is mandatory for delete images");
        } else {
            try {
                images = packageSlideRepository.findByPackageId(packageId);
                if (images.isEmpty()) {
                    log.warn("No images found for the given Package ID");
                } else {
                    packageSlideRepository.deleteAllByPackageId(packageId);

                    for (PackageSlide image : images) {
                        deleteFile(image.getFilePath());
                    }

                    //implement a file delete part here for given path.
                    log.info("Images deleted successfully");
                }
            } catch (Exception ex) {
                log.error("Error occurred while retrieving images: {}", ex.getMessage(), ex);
            }
        }
    }

    private void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
            log.info("Deleted file: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", filePath, e);
        }
    }

    private String generateFileName(String packageId, MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = (originalFileName != null && originalFileName.contains("."))
                ? originalFileName.substring(originalFileName.lastIndexOf("."))
                : "";
        return packageId + fileExtension;
    }
    private PackageSlide saveToDatabase(String packageId, String fileName, String filePath) {
        PackageSlide slide = new PackageSlide();
        slide.setPackageId(packageId);
        slide.setFileName(fileName);
        slide.setFilePath(filePath);
        packageSlideRepository.save(slide);
        return slide;
    }

    private String uploadFile(MultipartFile file, String packageId, String fileName) {
        try {
            String uploadPath = getUploadPath();
            File packageDir = new File(uploadPath + File.separator + packageId);

            // Create directory if it doesn't exist
            if (!packageDir.exists()) {
                packageDir.mkdirs();
            }

            Path path = Paths.get(packageDir.getAbsolutePath(), fileName);
            Files.write(path, file.getBytes());

            return path.toString();
        } catch (IOException e) {
            log.error("Error uploading file", e);
            return null;
        }
    }


    private String getUploadPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return windowsUploadPath;
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            return linuxUploadPath;
        } else {
            throw new UnsupportedOperationException("Unsupported operating system");
        }
    }
}