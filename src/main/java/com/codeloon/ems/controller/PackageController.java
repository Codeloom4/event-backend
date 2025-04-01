package com.codeloon.ems.controller;

import com.codeloon.ems.dto.PackageDto;
import com.codeloon.ems.dto.PackageItemDto;
import com.codeloon.ems.service.ImageUploadService;
import com.codeloon.ems.service.PackageService;
import com.codeloon.ems.util.ResponseBean;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/ems/package")
@RequiredArgsConstructor
public class PackageController {
    private final PackageService packageService;
    private final ImageUploadService imageUploadService;

    @GetMapping("/access")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<?> access() {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = packageService.access();
            httpStatus = HttpStatus.CREATED;
        } catch (Exception ex) {
            log.error("Error occurred while accessing package management.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<?> createPackage(@Valid @RequestBody PackageDto pack) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = packageService.createPackage(pack);
            httpStatus = HttpStatus.CREATED;
        } catch (Exception ex) {
            log.error("Error occurred while saving new package.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<?> updatePackage(@Valid @RequestBody PackageDto pack) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = packageService.updatePackage(pack);
            httpStatus = HttpStatus.OK;
        } catch (Exception ex) {
            log.error("Error occurred while updating package.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    @GetMapping("/{eventType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('CLIENT')")
    public ResponseEntity<?> getPackagesByEventType( @PathVariable String eventType) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = packageService.getPackagesByEventType(eventType);
            httpStatus = HttpStatus.CREATED;
        } catch (Exception ex) {
            log.error("Error occurred while accessing package management.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    @DeleteMapping("/{packageId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<?> deletePackage(@PathVariable String packageId) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = packageService.deletePackage(packageId);
            httpStatus = HttpStatus.OK;
        } catch (Exception ex) {
            log.error("Error occurred while deleting package.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    @PostMapping("/item")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<?> createPackageItem(@Valid @RequestBody PackageItemDto packItem) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = packageService.createPackageItem(packItem);
            httpStatus = HttpStatus.CREATED;
        } catch (Exception ex) {
            log.error("Error occurred while saving new package item.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }
    @PutMapping("/item/{packageId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<?> updatePackageItem(@PathVariable String packageId, @Valid @RequestBody PackageItemDto packItem) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = packageService.updatePackageItem(packageId, packItem);
            httpStatus = HttpStatus.CREATED;
        } catch (Exception ex) {
            log.error("Error occurred while saving new package item.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }
    @GetMapping("/item/{packageId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<?> getPackageItems(@PathVariable String packageId) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = packageService.getPackageItems(packageId);
            httpStatus = HttpStatus.CREATED;
        } catch (Exception ex) {
            log.error("Error occurred while retrieving new package items.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }
    @DeleteMapping("/item/{packageId}/{itemCode}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<?> deletePackageItem(@PathVariable String packageId, @PathVariable String itemCode) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = packageService.deletePackageItem(itemCode, packageId);
            httpStatus = HttpStatus.OK;
        } catch (Exception ex) {
            log.error("Error occurred while deleting package item.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    @PostMapping("/images")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<?> uploadImages(@RequestParam("packageId") String packageId,
                                          @RequestParam("files") MultipartFile[] files) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = imageUploadService.uploadImages(packageId, files);
            httpStatus = HttpStatus.CREATED;
        } catch (Exception ex) {
            log.error("Error occurred while uploading package images .{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }
    @GetMapping("/images/{packageId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<?> getImages(@PathVariable String packageId) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = imageUploadService.getImages(packageId);
            httpStatus = HttpStatus.CREATED;
        } catch (Exception ex) {
            log.error("Error occurred while uploading package images .{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

}
