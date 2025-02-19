package com.codeloon.ems.service;

import com.codeloon.ems.dto.PaymentDto;
import com.codeloon.ems.util.ResponseBean;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface PaymentService {
    ResponseBean createPayment(PaymentDto paymentDto, MultipartFile file);
    ResponseBean getPaymentById(Long id);
    ResponseBean getAllPayments();
    Resource downloadFile(Long id);
}

