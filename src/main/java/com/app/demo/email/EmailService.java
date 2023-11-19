package com.app.demo.email;

import com.app.demo.utils.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface EmailService {
    ResponseEntity<GenericResponse> sendEmail(EmailDto emailDto);
}
