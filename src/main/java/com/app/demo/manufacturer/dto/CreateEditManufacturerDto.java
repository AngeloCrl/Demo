package com.app.demo.manufacturer.dto;

import com.app.demo.car.dto.CarIdDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEditManufacturerDto {
    private Long id;
    @ApiModelProperty(position = 1)
    private String name;
    @ApiModelProperty(position = 2)
    private List<CarIdDto> cars;
}
