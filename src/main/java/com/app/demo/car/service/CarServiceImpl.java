package com.app.demo.car.service;

import com.app.demo.car.dto.CarResponseDto;
import com.app.demo.car.dto.CreateUpdateCarDto;
import com.app.demo.car.model.Car;
import com.app.demo.car.repository.CarRepository;
import com.app.demo.car.repository.projection.ProjectionDto;
import com.app.demo.manufacturer.dto.ManufacturerIdDto;
import com.app.demo.user.dto.UserIdDto;
import com.app.demo.user.model.User;
import com.app.demo.user.repository.UserRepository;
import com.app.demo.utils.AppUtils;
import com.app.demo.utils.exception.CustomException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private ModelMapper strictModelMapper;
    private static final Logger logger = LoggerFactory.getLogger(CarServiceImpl.class);

    @PostConstruct
    private void setStrictModelMapper() {
        strictModelMapper = AppUtils.initStrictModelMapper();
    }

    @Autowired
    public CarServiceImpl(CarRepository carRepository, UserRepository userRepository) {
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void createUpdate(CreateUpdateCarDto createUpdateCarDto) {
        AppUtils.logObject(createUpdateCarDto, "Creating - Editing Car");
        try {
            Car car = createUpdateCarDto.getId() == null ? new Car() : carRepository.findById(createUpdateCarDto.getId())
                    .orElseThrow(() -> new CustomException(String.format("Car with id %s doesn't exist", createUpdateCarDto.getId()), HttpStatus.NOT_FOUND));

            manageAssignments(car, createUpdateCarDto);
            strictModelMapper.map(createUpdateCarDto, car);
            carRepository.save(car);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Creating - Editing Car", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void manageAssignments(Car car, CreateUpdateCarDto createUpdateCarDto) {
        ManufacturerIdDto manufacturerIdDto = createUpdateCarDto.getManufacturer();
        UserIdDto ownerIdDto = createUpdateCarDto.getOwner();

        if (manufacturerIdDto == null && car.getManufacturer() != null) {
            createUpdateCarDto.setManufacturer(new ManufacturerIdDto(car.getManufacturer().getId()));
        }
        car.setManufacturer(null);

        if (ownerIdDto == null && car.getOwner() != null) {
            createUpdateCarDto.setOwner(new UserIdDto(car.getOwner().getId()));
        }
        if (ownerIdDto != null) {
            car.setOwner(null);
        }
    }

    @Override
    public CarResponseDto findById(Long id) {
        logger.info("Fetching Car By Id");
        Car car = carRepository.findById(id).orElseThrow(() -> new CustomException(String.format("Car with id %s doesn't exist", id), HttpStatus.NOT_FOUND));
        return strictModelMapper.map(car, CarResponseDto.class);
    }

    @Override
    public List<CarResponseDto> findAll() {
        logger.info("Fetching All Cars");
        try {
            return carRepository.findAll().stream().map(car -> strictModelMapper.map(car, CarResponseDto.class)).toList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Fetching All Cars", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<ProjectionDto> findAllUsingProjection() {
        logger.info("Fetching All Cars Using Projection");
        try {
            return carRepository.getResults().stream().map(res -> strictModelMapper.map(res, ProjectionDto.class)).toList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Fetching All Cars Using Projection", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        logger.info("Deleting Car");
        try {
            User user = userRepository.findByCars_Id(id).orElseThrow(() -> new CustomException(String.format("User with carId %s doesn't exist", id), HttpStatus.NOT_FOUND));
            Car car = carRepository.findById(id).orElseThrow(() -> new CustomException(String.format("Car with id %s doesn't exist", id), HttpStatus.NOT_FOUND));
            user.removeCar(car);
            userRepository.save(user);
            carRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Deleting Car", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

