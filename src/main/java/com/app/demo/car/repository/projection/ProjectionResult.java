package com.app.demo.car.repository.projection;

import java.time.LocalDate;

public interface ProjectionResult {
    Long getId();
    String getBrand();
    String getModel();
    LocalDate getCreationDate();
    LocalDate getLastModDate();
    String getManufacturer();
}
