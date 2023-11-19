package com.app.demo.car.dto;

import com.app.demo.user.dto.UserIdDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarResponseDto {
    @ApiModelProperty(position = 1)
    private Long id;
    @ApiModelProperty(position = 2)
    private String brand;
    @ApiModelProperty(position = 3)
    private String model;
    @ApiModelProperty(position = 4)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime creationDate;
    @ApiModelProperty(position = 5)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime lastModDate;
    @ApiModelProperty(position = 6)
    private UserIdDto owner;
}
