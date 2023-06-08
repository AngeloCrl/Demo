package com.app.demo.car.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUpdateCarDto {
    private Long id;
    private String brand;
    private String model;
    private Long manufacturerId;
    private Long ownerId;
}
