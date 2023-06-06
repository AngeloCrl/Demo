package com.app.demo.car.service;

import com.app.demo.car.dto.CarDto;
import com.app.demo.car.dto.CreateUpdateCarDto;
import com.app.demo.car.dto.ProjectionDto;
import com.app.demo.car.model.Car;
import com.app.demo.car.repository.CarRepository;
import com.app.demo.car.repository.ProjectionResult;
import com.app.demo.manufacturer.model.Manufacturer;
import com.app.demo.manufacturer.repository.ManufacturerRepository;
import com.app.demo.utils.AppHelper;
import com.app.demo.utils.exception.CustomException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final ManufacturerRepository manufacturerRepository;
    private static final String NOT_FOUND = "Entity not found";
    private ModelMapper strictModelMapper;

    @PostConstruct
    private void setStrictModelMapper() {
        strictModelMapper = AppHelper.initStrictModelMapper();
    }

    @Autowired
    public CarServiceImpl(CarRepository carRepository, ManufacturerRepository manufacturerRepository) {
        this.carRepository = carRepository;
        this.manufacturerRepository = manufacturerRepository;
    }

    @Override
    public void createUpdate(CreateUpdateCarDto createUpdateCarDto) {
        AppHelper.logObject(createUpdateCarDto, "Creating - Editing Car");
        try {
            Car car = new Car();
            if (createUpdateCarDto.getId() != null) {
                car = carRepository.findById(createUpdateCarDto.getId()).orElseThrow(() -> new CustomException(NOT_FOUND, HttpStatus.NOT_FOUND));
            }
            if (createUpdateCarDto.getManufacturerId() != null) {
                assignToManufacturerAndSave(createUpdateCarDto, car);
            } else {
                strictModelMapper.map(createUpdateCarDto, car);
                carRepository.save(car);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Editing Manufacturer", HttpStatus.BAD_REQUEST);
        }
    }

    private void assignToManufacturerAndSave(CreateUpdateCarDto createDto, Car car){
        Manufacturer manufacturer = manufacturerRepository.findById(createDto.getManufacturerId())
                .orElseThrow(() -> new CustomException(NOT_FOUND, HttpStatus.NOT_FOUND));
        createDto.setBrand(manufacturer.getName());
        strictModelMapper.map(createDto, car);
        manufacturer.addCar(car);
        manufacturerRepository.save(manufacturer);
    }

    @Override
    public CarDto findById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new CustomException(NOT_FOUND, HttpStatus.NOT_FOUND));
        return strictModelMapper.map(car, CarDto.class);
    }

    @Override
    public List<CarDto> findAll() {
        return carRepository.findAll().stream().map(car -> strictModelMapper.map(car, CarDto.class)).toList();
    }

    public List<ProjectionDto> findAllUsingProjection() {
        List<ProjectionDto> carDtos = new ArrayList<>();
        List<ProjectionResult> results = carRepository.getResults();
        results.forEach(res -> {
            ProjectionDto carDto = new ProjectionDto();
            carDto.setId(res.getId());
            carDto.setBrand(res.getBrand());
            carDto.setModel(res.getModel());
            carDto.setCreationDate(res.getCreationDate());
            carDto.setLastModDate(res.getLastModDate());
            carDto.setManufacturer(res.getManufacturer());
            carDtos.add(carDto);
        });
        return carDtos;
    }
}

