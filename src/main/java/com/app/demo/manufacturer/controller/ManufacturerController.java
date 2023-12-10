package com.app.demo.manufacturer.controller;

import com.app.demo.manufacturer.dto.CreateEditManufacturerDto;
import com.app.demo.manufacturer.dto.ManufacturerResponseDto;
import com.app.demo.manufacturer.service.ManufacturerService;
import com.app.demo.utils.GenericResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manufacturer")
@Api(tags = "Manufacturer")
public class ManufacturerController {

    @Autowired
    private final ManufacturerService manufacturerService;

    public ManufacturerController(ManufacturerService manufacturerService) {
        this.manufacturerService = manufacturerService;
    }

    @PostMapping("/create-update")
    @ApiOperation(value = "ManufacturerController.createUpdate")
    public ResponseEntity<GenericResponse> createUpdate(@ApiParam("CreateEditManufacturerDto")
                                                        @RequestBody CreateEditManufacturerDto dto) {
        manufacturerService.createUpdate(dto);
        return new ResponseEntity<>(new GenericResponse("Manufacturer Created or Updated!"), HttpStatus.OK);
    }

    @GetMapping(value = "{id}")
    @ApiOperation(value = "ManufacturerController.findById")
    public ResponseEntity<ManufacturerResponseDto> findById(@PathVariable("id") Long id) {
        ManufacturerResponseDto manufacturer = manufacturerService.findById(id);
        return new ResponseEntity<>(manufacturer, HttpStatus.OK);
    }

    @GetMapping("/all")
    @ApiOperation(value = "ManufacturerController.findALL")
    public ResponseEntity<List<ManufacturerResponseDto>> findAll() {
        List<ManufacturerResponseDto> manufacturers = manufacturerService.findAll();
        return new ResponseEntity<>(manufacturers, HttpStatus.OK);
    }

    @DeleteMapping(value = "/id/{id}")
    @ApiOperation(value = "ManufacturerController.delete")
    public ResponseEntity<GenericResponse> delete(@PathVariable("id") Long id) {
        manufacturerService.delete(id);
        return new ResponseEntity<>(new GenericResponse("Deleted Successfully"), HttpStatus.OK);
    }
}
