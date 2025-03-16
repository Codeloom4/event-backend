package com.codeloon.ems.controller;

import com.codeloon.ems.model.EventBean;
import com.codeloon.ems.service.EventService;
import com.codeloon.ems.service.GalleryService;
import com.codeloon.ems.service.PackageService;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class HomeController {

    private final EventService eventService;
    private final GalleryService galleryService;
    private final PackageService packageService;

    @GetMapping("/")
    public String getHome() {
        return "Hello World";
    }

    @GetMapping("/services")
    public ResponseEntity<?> getAllEvents() {
        ResponseEntity<?> responseEntity;
        ResponseBean responseBean = new ResponseBean();
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        try {
            List<EventBean> events = eventService.getAllEvents();
            responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
            responseBean.setResponseMsg("Events retrieved successfully.");
            responseBean.setContent(events);
            httpStatus = HttpStatus.OK;
        } catch (Exception ex) {
            log.error("Error occurred while retrieving public event list: {}", ex.getMessage());
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error occurred while retrieving events.");
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    // You can add more public endpoints here for other data as needed
    // For example:

    // @GetMapping("/package-types")
    // @GetMapping("/about")


    // New endpoint to get gallery images with event descriptions
    @GetMapping("/gallery")
    public ResponseEntity<?> getGalleryImages() {
        ResponseEntity<?> responseEntity;
        ResponseBean responseBean = new ResponseBean();
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        try {
            // Use the existing GalleryService method to fetch gallery data
            ResponseBean galleryResponse = galleryService.getAllImages();
            responseBean.setResponseCode(galleryResponse.getResponseCode());
            responseBean.setResponseMsg(galleryResponse.getResponseMsg());
            responseBean.setContent(galleryResponse.getContent());
            httpStatus = HttpStatus.OK;
        } catch (Exception ex) {
            log.error("Error occurred while retrieving gallery images: {}", ex.getMessage());
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error occurred while retrieving gallery images.");
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    @GetMapping("/all-package")
    public ResponseEntity<?> getAllPackages() {
        ResponseEntity<?> responseEntity;
        ResponseBean responseBean = new ResponseBean();
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        try {
            responseBean = packageService.getAllPackages();
            httpStatus = HttpStatus.OK;
        } catch (Exception ex) {
            log.error("Error occurred while retrieving package data getAllPackages : {}", ex.getMessage());
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error occurred while retrieving package data .");
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }


}

