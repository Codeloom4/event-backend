package com.codeloon.ems.service.impl;

import com.codeloon.ems.model.EmailRequestBean;
import com.codeloon.ems.service.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;


    @Override
    public void sendPlainTextEmail(EmailRequestBean emailRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(emailRequest.getTo());
        if(emailRequest.getCc()!=null) {
            message.setCc(emailRequest.getCc());
        }
        if(emailRequest.getBcc()!=null) {
            message.setBcc(emailRequest.getBcc());
        }
        message.setSubject(emailRequest.getSubject());
        message.setText(emailRequest.getText());
        mailSender.send(message);
    }
}
