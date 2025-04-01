package com.codeloon.ems.controller;

import com.codeloon.ems.service.SystemUserStatusService;
import com.codeloon.ems.util.ResponseBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/ems/reports/system-user-status")
@RequiredArgsConstructor
public class SystemUserStatusController {

    private final SystemUserStatusService systemUserStatusService;

    @GetMapping("/list")
    public ResponseEntity<?> getSystemUserStatusList() {
        return ResponseEntity.ok(systemUserStatusService.getSystemUserStatusList());
    }

    @GetMapping("/generate-excel")
    public ResponseEntity<byte[]> generateSystemUserStatusExcel() {
        byte[] excelContent = systemUserStatusService.generateSystemUserStatusExcel();
        String fileName = systemUserStatusService.getExcelFileName();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelContent);
    }
}

