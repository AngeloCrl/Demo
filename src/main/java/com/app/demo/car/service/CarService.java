package com.app.demo.car.service;

import com.app.demo.car.dto.CarDto;
import com.app.demo.car.dto.CreateUpdateCarDto;
import com.app.demo.car.dto.ProjectionDto;

import java.util.List;

public interface CarService {
    void createUpdate(CreateUpdateCarDto createDto);
    CarDto findById(Long id);
    List<CarDto> findAll();
    List<ProjectionDto> findAllUsingProjection();
    void delete(Long id);
}
