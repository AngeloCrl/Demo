package com.app.demo.utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse {

    private String message;
    private String error;

    public GenericResponse(String message) {
        super();
        this.message = message;
    }
}
