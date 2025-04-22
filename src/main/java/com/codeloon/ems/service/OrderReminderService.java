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
        LocalDateTime startOfDay = oneYearAgoDate.atStartOfDay();
        LocalDateTime endOfDay = oneYearAgoDate.atTime(LocalTime.MAX);
        LocalDate oneYearAgoDateTest = LocalDate.now().minusYears(1);
        System.out.println(" new date ********************"+oneYearAgoDateTest);
        Optional<Status> paymentApprovedStatus = statusRepository.findById("PAYMENT_APPROVED");

        List<OrderRequest> orders = OrderRequestRepository
                .findByRequestedDateBetweenAndPaymentStatus(startOfDay, endOfDay, paymentApprovedStatus.get());

        for (OrderRequest order : orders) {
//            Optional<User> userDetails = userRepository.findByUsername(String.valueOf(order.getCustomerUsername()));
//            userDetails.ifPresent(user -> sendEmail(order.getCustomerUsername().getEmail(), order));
            sendEmail(order.getCustomerUsername().getEmail(), order);
        }

    }

//    private void sendEmail(String toEmail, OrderRequest order) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(toEmail);
//        message.setSubject("Last Year Order Reminder");
//        String text = "Dear Customer, It's been a year since your order. " + order.getCustomerNote() + ", We appreciate your support!";
//        message.setText(text);
//
//        mailSender.send(message);
//    }

    private void sendEmail(String toEmail, OrderRequest order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("🎉 Ready to Celebrate Again? Let’s Plan Your Next Event!");

        String text = "💌 Dear Valued Customer,\n\n" +
                "We hope you’re doing wonderfully! 🌟 It’s hard to believe it’s already been a whole year since we had the pleasure of organizing your event.\n\n" +
                "📅 Your note from that time: \"" + order.getCustomerNote() + "\" really touched us — thank you again for letting us be part of your special day!\n\n" +
                "As a new season of celebrations begins, we’d love to help you make this year’s event even more unforgettable. 🎊 Whether it’s a birthday, wedding, corporate gathering, or any special occasion, our team is here to bring your vision to life.\n\n" +
                "📞 Let’s reconnect and start planning something amazing together! You can reach us anytime at:\n" +
                "🌐 www.partyCraft.com\n" +
                "We can’t wait to hear from you!\n\n" +
                "With warm wishes,\n" +
                "✨ partyCrafting";

        message.setText(text);

        mailSender.send(message);
    }
}
