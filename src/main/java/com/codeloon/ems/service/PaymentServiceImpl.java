package com.codeloon.ems.service;

import com.codeloon.ems.dto.PaymentDto;
import com.codeloon.ems.entity.Payment;
import com.codeloon.ems.repository.PaymentRepository;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${file.upload.windows}")
    private String windowsUploadPath;

    @Value("${file.upload.linux}")
    private String linuxUploadPath;

    @Override
    @Transactional
    public ResponseBean createPayment(PaymentDto paymentDto, MultipartFile file) {
        ResponseBean responseBean = new ResponseBean();
        try {
            String filePath = uploadFile(file);
            if (filePath == null) {
                responseBean.setResponseCode(ResponseCode.RSP_ERROR);
                responseBean.setResponseMsg("Failed to upload file.");
                return responseBean;
            }

            Payment payment = new Payment();
            BeanUtils.copyProperties(paymentDto, payment);
            payment.setFilePath(filePath);
            //payment.setCreatedAt(new Date());

            Payment savedPayment = paymentRepository.save(payment);
            responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
            responseBean.setResponseMsg("Payment created successfully.");
            responseBean.setContent(savedPayment);
        } catch (Exception e) {
            log.error("Error creating payment", e);
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error creating payment: " + e.getMessage());
        }
        return responseBean;
    }

    @Override
    public ResponseBean getPaymentById(Long id) {
        ResponseBean responseBean = new ResponseBean();
        try {
            Payment payment = paymentRepository.findById(id).orElse(null);
            if (payment != null) {
                responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
                responseBean.setResponseMsg("Payment retrieved successfully.");
                responseBean.setContent(payment);
            } else {
                responseBean.setResponseCode(ResponseCode.RSP_ERROR);
                responseBean.setResponseMsg("Payment not found.");
            }
        } catch (Exception e) {
            log.error("Error retrieving payment", e);
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error retrieving payment: " + e.getMessage());
        }
        return responseBean;
    }

    @Override
    public ResponseBean getAllPayments() {
        ResponseBean responseBean = new ResponseBean();
        try {
            List<Payment> payments = paymentRepository.findAll();
            responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
            responseBean.setResponseMsg("Payments retrieved successfully.");
            responseBean.setContent(payments);
        } catch (Exception e) {
            log.error("Error retrieving payments", e);
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error retrieving payments: " + e.getMessage());
        }
        return responseBean;
    }

    private String uploadFile(MultipartFile file) {
        try {
            String uploadPath = getUploadPath();
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadPath + fileName);
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

    @Override
    public Resource downloadFile(Long id) {
        try {
            Payment payment = paymentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            Path filePath = Paths.get(payment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

}

