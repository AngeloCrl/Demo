package com.app.demo.email;

import com.app.demo.utils.dto.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface EmailService {
    ResponseEntity<GenericResponse> sendEmail(EmailDto emailDto);
}
