package com.app.demo.manufacturer.service;

import com.app.demo.manufacturer.dto.CreateEditManufacturerDto;
import com.app.demo.manufacturer.dto.ManufacturerResponseDto;

import java.util.List;

public interface ManufacturerService {
    void createUpdate(CreateEditManufacturerDto dto);
    ManufacturerResponseDto findById(Long id);
    List<ManufacturerResponseDto> findAll();
    void delete(Long id);
}
