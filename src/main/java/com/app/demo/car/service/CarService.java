package com.app.demo.car.service;

import com.app.demo.car.dto.CarResponseDto;
import com.app.demo.car.dto.CreateUpdateCarDto;
import com.app.demo.car.repository.projection.ProjectionDto;

import java.util.List;

public interface CarService {
    void createUpdate(CreateUpdateCarDto createDto);
    CarResponseDto findById(Long id);
    List<CarResponseDto> findAll();
    List<ProjectionDto> findAllUsingProjection();
    void delete(Long id);
}
