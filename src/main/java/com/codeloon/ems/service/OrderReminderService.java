package com.codeloon.ems.service;

import com.codeloon.ems.entity.OrderRequest;
import com.codeloon.ems.entity.Status;
import com.codeloon.ems.entity.User;
import com.codeloon.ems.repository.OrderRequestRepository;
import com.codeloon.ems.repository.StatusRepository;
import com.codeloon.ems.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderReminderService {

    @Autowired
    private OrderRequestRepository OrderRequestRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at midnight
//    @Scheduled(cron = "0 * * * * ?") // Runs every minute
    public void sendReminderEmails() {
        LocalDate oneYearAgoDate = LocalDate.now().minusYears(1);
        LocalDateTime startOfDay = oneYearAgoDate.atStartOfDay();               // 2024-04-06T00:00
        LocalDateTime endOfDay = oneYearAgoDate.atTime(LocalTime.MAX);          // 2024-04-06T23:59:59.999999999

        Optional<Status> paymentApprovedStatus = statusRepository.findById("PAYMENT_APPROVED");

        List<OrderRequest> orders = OrderRequestRepository
                .findByRequestedDateBetweenAndOrderStatus(startOfDay, endOfDay, paymentApprovedStatus.get());

        for (OrderRequest order : orders) {
//            Optional<User> userDetails = userRepository.findByUsername(String.valueOf(order.getCustomerUsername()));
//            userDetails.ifPresent(user -> sendEmail(order.getCustomerUsername().getEmail(), order));
            sendEmail(order.getCustomerUsername().getEmail(), order);
        }

    }

    private void sendEmail(String toEmail, OrderRequest order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Last Year Order Reminder");
        String text = "Dear Customer, It's been a year since your order. " + order.getCustomerNote() + ", We appreciate your support!";
        message.setText(text);

        mailSender.send(message);
    }
}
