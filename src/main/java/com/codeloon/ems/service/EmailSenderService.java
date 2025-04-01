package com.codeloon.ems.service;

import com.codeloon.ems.model.EmailRequestBean;

public interface EmailSenderService {
    void sendPlainTextEmail(EmailRequestBean emailRequest);
}
