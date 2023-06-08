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
import java.util.List;

@Service
public class ManufacturerServiceImpl implements ManufacturerService {
    private final ManufacturerRepository manufacturerRepository;
    private ModelMapper strictModelMapper;
    private static final String NOT_FOUND = "Entity not found";
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
    public void createUpdate(ManufacturerDto createUpdateDto) {
        AppHelper.logObject(createUpdateDto, "Creating - Updating Manufacturer");
        Manufacturer manufacturer = new Manufacturer();
        if (createUpdateDto.getCars() != null) {
            createUpdateDto.getCars().forEach(car -> car.setBrand(createUpdateDto.getName()));
        }
        if (createUpdateDto.getId() != null) {
            manufacturer = manufacturerRepository.findById(createUpdateDto.getId()).orElseThrow(() -> new CustomException(NOT_FOUND, HttpStatus.NOT_FOUND));
        }
        try {
            strictModelMapper.map(createUpdateDto, manufacturer);
            manufacturerRepository.save(manufacturer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Creating - Updating Manufacturer", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ManufacturerDto findById(Long id) {
        logger.info("Fetching Manufacturer By Id");
        try {
            Manufacturer manufacturer = manufacturerRepository.findById(id).orElseThrow(() -> new CustomException(NOT_FOUND, HttpStatus.NOT_FOUND));
            return strictModelMapper.map(manufacturer, ManufacturerDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Fetching Manufacturer By Id", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<ManufacturerDto> findAll() {
        logger.info("Fetching All Manufacturers");
        try {
            return manufacturerRepository.findAll().stream().map(manufacturer -> strictModelMapper.map(manufacturer, ManufacturerDto.class)).toList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Fetching All Manufacturers", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting Manufacturer");
        try {
            manufacturerRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Deleting Manufacturer", HttpStatus.BAD_REQUEST);
        }
    }
}
