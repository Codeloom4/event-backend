package com.codeloon.ems.service;

import com.codeloon.ems.entity.Event;
import com.codeloon.ems.model.EmailRequestBean;
import com.codeloon.ems.model.EventBean;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderMailService {

    private final EmailSenderService emailSenderService;
    private final EventService eventService;

    // Hardcoded values for demonstration
    private final String userEmail = "ashenjz072@gmail.com";
    private final String userName = "John Doe";
    private final String websiteUrl = "http://localhost:5000";

    // List of services
    private final List<String> services = List.of(
            "Service 1 - New feature or improvement",
            "Service 2 - Another great benefit",
            "Service 3 - More convenience for you"
    );

    @Scheduled(cron = "0 24 20 * * ?")
    public void sendReminderMail() {
        LocalDate elevenMonthsAgo = LocalDate.now().minusMonths(11);
        System.out.println("elevenMonthsAgo = " + elevenMonthsAgo);
//        List<EventBean> events = eventService.getAllEvents();
        System.out.println("--------------------------------------------Sending reminder email--------------------------------------------");
        String subject = "We’ve Got Some Exciting Updates for You! 🎉";

        // Construct service list as a string
        StringBuilder serviceList = new StringBuilder();
        for (String service : services) {
            serviceList.append("✅ ").append(service).append("\n");
        }

        String text = "Hey " + userName + ",\n\n"
                + "Hope you're doing great! It’s been a while since you last booked an event with us, "
                + "and we just wanted to check in. Since then, we’ve been working hard to improve our services "
                + "and add even more awesome features!\n\n"
                + "Here’s what’s new:\n"
                + serviceList.toString() + "\n"
                + "We’d love to have you back and help make your next event even better! "
                + "Check out what we have to offer:\n\n"
                + "👉 " + websiteUrl + "\n\n"
                + "Let us know if you have any questions—we're happy to help!\n\n"
                + "Cheers,\n"
                + "Your Event Team";

        EmailRequestBean emailRequest = new EmailRequestBean();
        emailRequest.setTo(userEmail);
        emailRequest.setSubject(subject);
        emailRequest.setText(text);

        System.out.println("+++++++++++++++++++++++++++" + emailRequest.getText() + "+++++++++++++++++++++++++++");

        emailSenderService.sendPlainTextEmail(emailRequest);
    }
}
