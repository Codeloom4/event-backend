package com.codeloon.ems.controller;

import com.codeloon.ems.model.GroupingBean;
import com.codeloon.ems.service.GroupingService;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;

@RestController
@RequestMapping("/ems/groupings")
@RequiredArgsConstructor
public class GroupingController {

    private final GroupingService groupingService;

  // Create an ObjectMapper with JavaTimeModule
     private final ObjectMapper objectMapper = new ObjectMapper()
             .registerModule(new JavaTimeModule());

     
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('CLIENT')")
    public ResponseEntity<?> createGrouping(
            @RequestParam("grouping") String groupingJson,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();

        try {
            // Convert JSON string to GroupingBean
            ObjectMapper objectMapper = new ObjectMapper();
            GroupingBean groupingBean = objectMapper.readValue(groupingJson, GroupingBean.class);

            // Call the service method
            responseBean = groupingService.createGrouping(groupingBean, file);
            httpStatus = HttpStatus.CREATED;
        } catch (Exception ex) {
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error creating grouping: " + ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }

        return responseEntity;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('CLIENT')")
    public ResponseEntity<List<GroupingBean>> getGroupingsByUsername(@RequestParam String username) {
        return ResponseEntity.ok(groupingService.getGroupingsByUsername(username));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('CLIENT')")
    public ResponseEntity<ResponseBean> deleteGrouping(@PathVariable Long id) {
        return ResponseEntity.ok(groupingService.deleteGrouping(id));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('CLIENT')")
    public ResponseEntity<?> updateGrouping(
            @PathVariable Long id,
            @RequestParam("grouping") String groupingJson,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();

        try {
            // Convert JSON string to GroupingBean using the configured ObjectMapper
            GroupingBean groupingBean = objectMapper.readValue(groupingJson, GroupingBean.class);

            // Call the service method
            responseBean = groupingService.updateGrouping(id, groupingBean, file);
            httpStatus = HttpStatus.OK;
        } catch (Exception ex) {
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error updating grouping: " + ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }

        return responseEntity;
    }




    @GetMapping("/{id}/process")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('CLIENT')")
    public ResponseEntity<?> processGrouping(@PathVariable Long id) {
        ResponseBean responseBean = groupingService.processGrouping(id);

        if (responseBean.getResponseCode().equals(ResponseCode.RSP_SUCCESS)) {
            // If successful, return the PDF file
            byte[] pdfBytes = (byte[]) responseBean.getContent();
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=grouping_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } else {
            // If there's an error, return the error response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBean);
        }
    }























}