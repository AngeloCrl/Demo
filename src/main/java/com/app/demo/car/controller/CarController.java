package com.app.demo.car.controller;

import com.app.demo.car.dto.CarDto;
import com.app.demo.car.dto.CreateUpdateCarDto;
import com.app.demo.car.dto.ProjectionDto;
import com.app.demo.car.service.CarServiceImpl;
import com.app.demo.utils.dto.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/car")
public class CarController {
    private final CarServiceImpl carServiceImpl;

    public CarController(CarServiceImpl carServiceImpl) {
        this.carServiceImpl = carServiceImpl;
    }

    @PostMapping("/create-update")
    public ResponseEntity<GenericResponse> createUpdate(@RequestBody CreateUpdateCarDto dto) {
        carServiceImpl.createUpdate(dto);
        return new ResponseEntity<>(new GenericResponse("Car created!"), HttpStatus.OK);
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<CarDto> findById(@PathVariable("id") Long id) {
        CarDto car = carServiceImpl.findById(id);
        return new ResponseEntity<>(car, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CarDto>> findAll() {
        List<CarDto> cars = carServiceImpl.findAll();
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    @GetMapping("/all/projection")
    public ResponseEntity<List<ProjectionDto>> findAllUsingProjection() {
        List<ProjectionDto> cars = carServiceImpl.findAllUsingProjection();
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }
}
