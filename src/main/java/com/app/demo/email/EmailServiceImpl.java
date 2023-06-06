package com.app.demo.email;

import com.app.demo.utils.AppHelper;
import com.app.demo.utils.dto.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender emailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public ResponseEntity<GenericResponse> sendEmail(EmailDto emailDto) {
        AppHelper.logObject(emailDto, "Sending Email");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDto.getRecipient());
        message.setSubject(emailDto.getSubject());
        message.setText(emailDto.getText());
        try {
            emailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new GenericResponse("Exception While Sending Registration Mail"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new GenericResponse("Registration Mail is Sent"), HttpStatus.OK);
    }
}
