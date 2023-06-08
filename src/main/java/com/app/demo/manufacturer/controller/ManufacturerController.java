package com.app.demo.manufacturer.controller;

import com.app.demo.manufacturer.dto.ManufacturerDto;
import com.app.demo.manufacturer.service.ManufacturerService;
import com.app.demo.utils.dto.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manufacturer")
public class ManufacturerController {

    @Autowired
    private final ManufacturerService manufacturerService;

    public ManufacturerController(ManufacturerService manufacturerService) {
        this.manufacturerService = manufacturerService;
    }

    @PostMapping("/create-update")
    public ResponseEntity<GenericResponse> createUpdate(@RequestBody ManufacturerDto dto) {
        manufacturerService.createUpdate(dto);
        return new ResponseEntity<>(new GenericResponse("Manufacturer Created or Updated!"), HttpStatus.OK);
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<ManufacturerDto> findById(@PathVariable("id") Long id) {
        ManufacturerDto manufacturer = manufacturerService.findById(id);
        return new ResponseEntity<>(manufacturer, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ManufacturerDto>> findAll() {
        List<ManufacturerDto> manufacturers = manufacturerService.findAll();
        return new ResponseEntity<>(manufacturers, HttpStatus.OK);
    }

    @DeleteMapping(value = "/id/{id}")
    public ResponseEntity<GenericResponse> delete(@PathVariable("id") Long id) {
        manufacturerService.delete(id);
        return new ResponseEntity<>(new GenericResponse("Deleted Successfully"), HttpStatus.OK);
    }
}
