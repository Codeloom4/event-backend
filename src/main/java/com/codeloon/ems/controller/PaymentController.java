package com.codeloon.ems.controller;

import com.codeloon.ems.dto.PaymentDto;
import com.codeloon.ems.service.PaymentService;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;


@Slf4j
@RestController
@RequestMapping("/ems/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper; // Inject ObjectMapper

    @PostMapping
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<?> createPayment(
            @RequestParam("payment") String paymentJson,
            @RequestParam("file") MultipartFile file) {

        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean response = new ResponseBean();

        try {
            // Convert JSON string to PaymentDto object
            PaymentDto paymentDto = objectMapper.readValue(paymentJson, PaymentDto.class);
            response = paymentService.createPayment(paymentDto, file);
            httpStatus = HttpStatus.CREATED;
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            log.error("Error occurred while make payment.{} ", ex.getMessage());
        }finally {
            responseEntity = new ResponseEntity<>(response, httpStatus);
        }
        return responseEntity;
    }

    @GetMapping("/{orderId}")  // Changed from {id}
    public ResponseEntity<ResponseBean> getPaymentByOrderId(@PathVariable String orderId) {  // Changed from Long id
        ResponseBean response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ResponseBean> getAllPayments() {
        ResponseBean response = paymentService.getAllPayments();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{orderId}/download")  // Changed from {id}
    public ResponseEntity<Resource> downloadFile(@PathVariable String orderId) {  // Changed from Long id
        Resource file = paymentService.downloadFile(orderId);
        // Extract the original filename and extension
        String originalFilename = file.getFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // Rename the file to include the orderId
        String newFilename = "Payment_" + orderId + extension;  // Changed from id to orderId
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFilename + "\"")
                .body(file);
    }
}