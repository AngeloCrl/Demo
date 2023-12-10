package com.app.demo.utils.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
public class ErrorObject {
    private int statusCode;
    private String message;
    private Date timestamp;
}
