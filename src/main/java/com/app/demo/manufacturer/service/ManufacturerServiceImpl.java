package com.app.demo.manufacturer.service;

import com.app.demo.manufacturer.dto.ManufacturerDto;
import com.app.demo.manufacturer.model.Manufacturer;
import com.app.demo.manufacturer.repository.ManufacturerRepository;
import com.app.demo.utils.AppHelper;
import com.app.demo.utils.exception.CustomException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ManufacturerServiceImpl implements ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;
    private ModelMapper strictModelMapper;
    private static final Logger logger = LoggerFactory.getLogger(ManufacturerServiceImpl.class);

    @PostConstruct
    private void setStrictModelMapper() {
        strictModelMapper = AppHelper.initStrictModelMapper();
    }

    @Autowired
    public ManufacturerServiceImpl(ManufacturerRepository manufacturerRepository) {
        this.manufacturerRepository = manufacturerRepository;
    }

    @Override
    public void create(ManufacturerDto createDto) {
        AppHelper.logObject(createDto, "Creating Manufacturer");
        if (createDto.getCars() != null) {
            createDto.getCars().forEach(car -> car.setBrand(createDto.getName()));
        }
        if(!manufacturerRepository.existsByNameIgnoreCase(createDto.getName())){
            Manufacturer manufacturer = strictModelMapper.map(createDto, Manufacturer.class);
            manufacturerRepository.save(manufacturer);
        }else {
            throw new CustomException("Manufacturer with name " + createDto.getName() + " already exists.", HttpStatus.CONFLICT);
        }
    }

    @Override
    public void edit(ManufacturerDto editDto, Long id) {
        AppHelper.logObject(editDto, "Editing Manufacturer");
        try {
            Manufacturer manufacturer = manufacturerRepository.findById(id).orElseThrow(() -> new CustomException("Entity not found", HttpStatus.NOT_FOUND));
            editDto.getCars().forEach(car -> car.setBrand(editDto.getName()));
            strictModelMapper.map(editDto, manufacturer);
            manufacturerRepository.save(manufacturer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Editing Manufacturer", HttpStatus.BAD_REQUEST);
        }
    }
}
