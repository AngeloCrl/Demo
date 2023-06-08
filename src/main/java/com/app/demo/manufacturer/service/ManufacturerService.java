package com.app.demo.manufacturer.service;

import com.app.demo.manufacturer.dto.ManufacturerDto;

import java.util.List;

public interface ManufacturerService {
    void createUpdate(ManufacturerDto dto);
    ManufacturerDto findById(Long id);
    List<ManufacturerDto> findAll();
    void delete(Long id);
}
