package com.codeloon.ems.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderReminderService {

//    @Autowired
//    private OrderRepository orderRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at midnight
//    @Scheduled(cron = "0 * * * * ?") // Runs every minute
    public void sendReminderEmails() {
//        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
//        List<Order> orders = orderRepository.findByOrderDate(oneYearAgo);

//        for (Order order : orders) {
//            sendEmail(order.getUser().getEmail(), order);
//        }
//        sendEmail(order.getUser().getEmail(), order);
        sendEmail();
    }

//    private void sendEmail(String toEmail, Order order) {
    private void sendEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(toEmail);
        message.setTo("damithrmdr@gmail.com");
        message.setSubject("Event Order Anniversary Reminder");
        message.setText("Dear Customer, It's been a year since your order. We appreciate your support!");

        mailSender.send(message);
    }
}
