package com.app.demo.car.dto;

import com.app.demo.manufacturer.dto.ManufacturerIdDto;
import com.app.demo.user.dto.UserIdDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUpdateCarDto {
    @ApiModelProperty(position = 1)
    private Long id;
    @ApiModelProperty(position = 2)
    private String brand;
    @ApiModelProperty(position = 3)
    private String model;
    @ApiModelProperty(position = 4)
    private ManufacturerIdDto manufacturer;
    @ApiModelProperty(position = 5)
    private UserIdDto owner;
}
