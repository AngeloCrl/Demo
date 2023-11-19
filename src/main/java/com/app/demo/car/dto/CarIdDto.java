package com.app.demo.car.dto;

import com.app.demo.user.dto.UserIdDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarIdDto {
    @ApiModelProperty(position = 1)
    private Long id;
    @ApiModelProperty(position = 2)
    private String brand;
    @ApiModelProperty(position = 3)
    private String model;
    @ApiModelProperty(position = 4)
    private UserIdDto owner;
}
