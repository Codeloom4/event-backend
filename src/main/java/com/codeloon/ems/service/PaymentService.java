package com.codeloon.ems.service;

import com.codeloon.ems.dto.PaymentDto;
import com.codeloon.ems.util.ResponseBean;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface PaymentService {
    ResponseBean createPayment(PaymentDto paymentDto, MultipartFile file);
    ResponseBean getPaymentByOrderId(String orderId);  // Changed from getPaymentById
    ResponseBean getAllPayments();
    Resource downloadFile(String orderId);  // Changed from Long id
}