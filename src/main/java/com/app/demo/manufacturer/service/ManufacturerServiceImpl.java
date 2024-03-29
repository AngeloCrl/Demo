package com.app.demo.manufacturer.service;

import com.app.demo.car.dto.CarIdDto;
import com.app.demo.car.model.Car;
import com.app.demo.car.repository.CarRepository;
import com.app.demo.manufacturer.dto.CreateEditManufacturerDto;
import com.app.demo.manufacturer.dto.ManufacturerResponseDto;
import com.app.demo.manufacturer.model.Manufacturer;
import com.app.demo.manufacturer.repository.ManufacturerRepository;
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
import java.util.Comparator;
import java.util.List;

@Service
public class ManufacturerServiceImpl implements ManufacturerService {
    private final ManufacturerRepository manufacturerRepository;
    private final CarRepository carRepository;
    private ModelMapper strictModelMapper;
    private static final Logger logger = LoggerFactory.getLogger(ManufacturerServiceImpl.class);

    @PostConstruct
    private void setStrictModelMapper() {
        strictModelMapper = AppUtils.initStrictModelMapper();
    }

    @Autowired
    public ManufacturerServiceImpl(ManufacturerRepository manufacturerRepository, CarRepository carRepository) {
        this.manufacturerRepository = manufacturerRepository;
        this.carRepository = carRepository;
    }


    @Override
    public void createUpdate(CreateEditManufacturerDto createUpdateDto) {
        AppUtils.logObject(createUpdateDto, "Creating - Updating Manufacturer");
        try {
            Manufacturer manufacturer = createUpdateDto.getId() == null ? new Manufacturer() : manufacturerRepository.findById(createUpdateDto.getId())
                    .orElseThrow(() -> new CustomException(String.format("Manufacturer with id %s doesn't exist", createUpdateDto.getId()), HttpStatus.NOT_FOUND));
            strictModelMapper.map(createUpdateDto, manufacturer);
            manufacturerRepository.save(manufacturer);
            assignCars(manufacturer, createUpdateDto);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Creating - Updating Manufacturer", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void assignCars(Manufacturer manufacturer, CreateEditManufacturerDto createUpdateDto) {
        logger.info("Assigning Cars To Manufacturer");
        if (createUpdateDto.getCars() != null) {
            createUpdateDto.getCars().forEach(carDto -> {
                Car car = carRepository.findById(carDto.getId()).orElseThrow(() -> new CustomException(String.format("Car with id %s doesn't exist", carDto.getId()), HttpStatus.NOT_FOUND));
                car.setManufacturer(manufacturer);
                carRepository.save(car);
            });
        }
    }

    @Override
    public ManufacturerResponseDto findById(Long id) {
        logger.info("Fetching Manufacturer By Id");
        try {
            Manufacturer manufacturer = manufacturerRepository.findById(id).orElseThrow(() -> new CustomException(String.format("Manufacturer with id %s doesn't exist", id), HttpStatus.NOT_FOUND));
            ManufacturerResponseDto manufacturerResponseDto = strictModelMapper.map(manufacturer, ManufacturerResponseDto.class);
            manufacturerResponseDto.setCars(carRepository.findByManufacturer_Id(id).stream().map(car -> strictModelMapper.map(car, CarIdDto.class)).toList());
            return manufacturerResponseDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Fetching Manufacturer By Id", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public List<ManufacturerResponseDto> findAll() {
        logger.info("Fetching All Manufacturers");
        try {
            return manufacturerRepository.findAll().stream().map(m -> {
                ManufacturerResponseDto manufacturerResponse = strictModelMapper.map(m, ManufacturerResponseDto.class);
                manufacturerResponse.setCars(carRepository.findByManufacturer_Id(m.getId()).stream().map(car -> strictModelMapper.map(car, CarIdDto.class)).toList());
                return manufacturerResponse;
            }).sorted(Comparator.comparing(ManufacturerResponseDto::getId)).toList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Fetching All Manufacturers", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting Manufacturer");
        try {
            // In order for the car to be deleted when we delete the manufacturer, we have to manage the car's associations first,
            // otherwise neither the manufacturer and the car will be deleted.
            // This is because in this case we have a unidirectional ManyToOne relationship between the car and the manufacturer,
            // and a bidirectional OneToMany relationship between the user and the car.
            carRepository.findByManufacturer_Id(id).forEach(car -> {
                car.setManufacturer(null);
                car.setOwner(null);
                carRepository.save(car);
                carRepository.delete(car);
            });
            manufacturerRepository.deleteById(id);

        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Deleting Manufacturer", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
