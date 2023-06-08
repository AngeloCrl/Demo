package com.app.demo.car.service;

import com.app.demo.car.dto.CarDto;
import com.app.demo.car.dto.CreateUpdateCarDto;
import com.app.demo.car.dto.ProjectionDto;
import com.app.demo.car.model.Car;
import com.app.demo.car.repository.CarRepository;
import com.app.demo.car.repository.ProjectionResult;
import com.app.demo.manufacturer.model.Manufacturer;
import com.app.demo.manufacturer.repository.ManufacturerRepository;
import com.app.demo.user.model.User;
import com.app.demo.user.repository.UserRepository;
import com.app.demo.utils.AppHelper;
import com.app.demo.utils.exception.CustomException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final UserRepository userRepository;
    private ModelMapper strictModelMapper;
    private static final String NOT_FOUND = "Entity not found";
    private static final Logger logger = LoggerFactory.getLogger(CarServiceImpl.class);

    @PostConstruct
    private void setStrictModelMapper() {
        strictModelMapper = AppHelper.initStrictModelMapper();
    }

    @Autowired
    public CarServiceImpl(CarRepository carRepository, ManufacturerRepository manufacturerRepository,
                          UserRepository userRepository) {
        this.carRepository = carRepository;
        this.manufacturerRepository = manufacturerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void createUpdate(CreateUpdateCarDto createUpdateCarDto) {
        AppHelper.logObject(createUpdateCarDto, "Creating - Editing Car");
        try {
            Car car = new Car();
            if (createUpdateCarDto.getId() != null) {
                car = carRepository.findById(createUpdateCarDto.getId()).orElseThrow(() -> new CustomException(NOT_FOUND, HttpStatus.NOT_FOUND));
            }
            if (createUpdateCarDto.getManufacturerId() != null && createUpdateCarDto.getOwnerId() != null) {
                assignToOwnerAndSave(createUpdateCarDto, car);
                assignToManufacturerAndSave(createUpdateCarDto, car);
            } else {
                strictModelMapper.map(createUpdateCarDto, car);
                carRepository.save(car);
            }
            if (createUpdateCarDto.getManufacturerId() != null) {
                assignToManufacturerAndSave(createUpdateCarDto, car);
            } else if (createUpdateCarDto.getOwnerId() != null) {
                assignToOwnerAndSave(createUpdateCarDto, car);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Creating or Editing Car", HttpStatus.BAD_REQUEST);
        }
    }

    private void assignToManufacturerAndSave(CreateUpdateCarDto createUpdateDto, Car car) {
        logger.info("Assigning Car To Manufacturer And Save");
        Manufacturer manufacturer = manufacturerRepository.findById(createUpdateDto.getManufacturerId())
                .orElseThrow(() -> new CustomException(NOT_FOUND, HttpStatus.NOT_FOUND));
        createUpdateDto.setBrand(manufacturer.getName());
        strictModelMapper.map(createUpdateDto, car);
        manufacturer.addCar(car);
        manufacturerRepository.save(manufacturer);
    }

    private void assignToOwnerAndSave(CreateUpdateCarDto createUpdateDto, Car car) {
        logger.info("Assigning Car To Owner And Save");
        User owner = userRepository.findById(createUpdateDto.getOwnerId())
                .orElseThrow(() -> new CustomException(NOT_FOUND, HttpStatus.NOT_FOUND));
        strictModelMapper.map(createUpdateDto, car);
        car.setOwner(owner);
        owner.addCar(car);
        userRepository.save(owner);
    }


    @Override
    public CarDto findById(Long id) {
        logger.info("Deleting Car");
        Car car = carRepository.findById(id).orElseThrow(() -> new CustomException(NOT_FOUND, HttpStatus.NOT_FOUND));
        return strictModelMapper.map(car, CarDto.class);
    }

    @Override
    public List<CarDto> findAll() {
        logger.info("Fetching All Cars");
        try {
            return carRepository.findAll().stream().map(car -> strictModelMapper.map(car, CarDto.class)).toList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Fetching All Cars", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<ProjectionDto> findAllUsingProjection() {
        logger.info("Fetching All Cars Using Projection");
        List<ProjectionDto> carDtos = new ArrayList<>();
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Fetching All Cars Using Projection", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting Car");
        try {
            carRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Deleting Car", HttpStatus.BAD_REQUEST);
        }
    }
}

