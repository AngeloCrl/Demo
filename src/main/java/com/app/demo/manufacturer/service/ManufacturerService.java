package com.app.demo.manufacturer.service;

import com.app.demo.manufacturer.dto.ManufacturerDto;

public interface ManufacturerService {

    void create(ManufacturerDto dto);

    void edit(ManufacturerDto dto, Long id);
}
