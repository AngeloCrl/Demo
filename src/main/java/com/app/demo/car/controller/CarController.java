package com.app.demo.car.controller;

import com.app.demo.car.dto.CarResponseDto;
import com.app.demo.car.dto.CreateUpdateCarDto;
import com.app.demo.car.repository.projection.ProjectionDto;
import com.app.demo.car.service.CarService;
import com.app.demo.utils.response.GenericResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/car")
@Api(tags = "Car")
public class CarController {
    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping("/create-update")
    @ApiOperation(value = "CarController.createUpdate")
    public ResponseEntity<GenericResponse> createUpdate(@ApiParam("CreateUpdateCarDto")
                                                        @RequestBody CreateUpdateCarDto dto) {
        carService.createUpdate(dto);
        return new ResponseEntity<>(new GenericResponse("Car Created or Updated!"), HttpStatus.CREATED);
    }

    @GetMapping(value = "{id}")
    @ApiOperation(value = "CarController.findById")
    public ResponseEntity<CarResponseDto> findById(@PathVariable("id") Long id) {
        CarResponseDto car = carService.findById(id);
        return new ResponseEntity<>(car, HttpStatus.OK);
    }

    @GetMapping("/all")
    @ApiOperation(value = "CarController.findByAll")
    public ResponseEntity<List<CarResponseDto>> findAll() {
        List<CarResponseDto> cars = carService.findAll();
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    @GetMapping("/all/projection")
    @ApiOperation(value = "CarController.findAllUsingProjection")
    public ResponseEntity<List<ProjectionDto>> findAllUsingProjection() {
        List<ProjectionDto> cars = carService.findAllUsingProjection();
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    @DeleteMapping(value = "/id/{id}")
    @ApiOperation(value = "CarController.delete")
    public ResponseEntity<GenericResponse> delete(@PathVariable("id") Long id) {
        carService.delete(id);
        return new ResponseEntity<>(new GenericResponse("Deleted Successfully"), HttpStatus.OK);
    }
}
