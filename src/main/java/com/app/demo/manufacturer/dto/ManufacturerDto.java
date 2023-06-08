package com.app.demo.manufacturer.dto;

import com.app.demo.car.dto.CreateUpdateCarDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManufacturerDto {
    private Long id;
    private String name;
    private List<CreateUpdateCarDto> cars;
}
