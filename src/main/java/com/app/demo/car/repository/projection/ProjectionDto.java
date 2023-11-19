package com.app.demo.car.repository.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectionDto {
    private Long id;
    private String brand;
    private String model;
    private LocalDate creationDate;
    private LocalDate lastModDate;
    private String manufacturer;
}
