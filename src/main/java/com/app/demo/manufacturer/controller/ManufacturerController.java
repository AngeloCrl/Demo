package com.app.demo.manufacturer.controller;

import com.app.demo.manufacturer.dto.ManufacturerDto;
import com.app.demo.manufacturer.service.ManufacturerService;
import com.app.demo.utils.dto.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manufacturer")
public class ManufacturerController {

    @Autowired
    private final ManufacturerService manufacturerService;

    public ManufacturerController(ManufacturerService manufacturerService) {
        this.manufacturerService = manufacturerService;
    }

    @PostMapping("/create")
    public ResponseEntity<GenericResponse> create(@RequestBody ManufacturerDto dto) {
        manufacturerService.create(dto);
        return new ResponseEntity<>(new GenericResponse("Manufacturer created!"), HttpStatus.OK);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<GenericResponse> edit(@RequestBody ManufacturerDto dto, @PathVariable("id")Long id) {
        manufacturerService.edit(dto, id);
        return new ResponseEntity<>(new GenericResponse("Manufacturer updated!"), HttpStatus.OK);
    }

}
