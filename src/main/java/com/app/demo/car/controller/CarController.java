package com.app.demo.car.controller;

import com.app.demo.car.dto.CarDto;
import com.app.demo.car.dto.CreateUpdateCarDto;
import com.app.demo.car.dto.ProjectionDto;
import com.app.demo.car.service.CarService;
import com.app.demo.utils.dto.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/car")
public class CarController {
    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping("/create-update")
    public ResponseEntity<GenericResponse> createUpdate(@RequestBody CreateUpdateCarDto dto) {
        carService.createUpdate(dto);
        return new ResponseEntity<>(new GenericResponse("Car Created or Updated!"), HttpStatus.OK);
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<CarDto> findById(@PathVariable("id") Long id) {
        CarDto car = carService.findById(id);
        return new ResponseEntity<>(car, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CarDto>> findAll() {
        List<CarDto> cars = carService.findAll();
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    @GetMapping("/all/projection")
    public ResponseEntity<List<ProjectionDto>> findAllUsingProjection() {
        List<ProjectionDto> cars = carService.findAllUsingProjection();
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    @DeleteMapping(value = "/id/{id}")
    public ResponseEntity<GenericResponse> delete(@PathVariable("id") Long id) {
        carService.delete(id);
        return new ResponseEntity<>(new GenericResponse("Deleted Successfully"), HttpStatus.OK);
    }
}
