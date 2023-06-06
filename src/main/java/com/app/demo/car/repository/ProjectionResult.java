package com.app.demo.car.repository;

import java.time.LocalDate;

public interface ProjectionResult {
    Long getId();
    String getBrand();
    String getModel();
    LocalDate getCreationDate();
    LocalDate getLastModDate();
    String getManufacturer();
}
