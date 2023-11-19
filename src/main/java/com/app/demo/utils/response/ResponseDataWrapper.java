package com.app.demo.utils.response;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDataWrapper<T> {
    private T data;
}