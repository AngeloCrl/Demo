package com.app.demo.user.dto;

import com.app.demo.car.dto.CreateUpdateCarDto;
import com.app.demo.user.validators.ValidPassword;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;

//@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @ApiModelProperty(example = "Walter")
    private Long id;
    @ApiModelProperty(position = 1, example = "Walter")
    private String firstName;
    @ApiModelProperty(position = 2, example = "White")
    private String lastName;
    @Email(message = "Email should be valid")
    @NotNull(message = "please specify an email address")
    @ApiModelProperty(position = 3, example = "w.white@gmail.com")
    private String email;
    @ValidPassword(message = "Password should be valid")
    @NotNull(message = "please specify a password")
    @ApiModelProperty(position = 4, example = "[{\"role\" : \"ROLE_CLIENT\"}]")
    private List<UserRoleDto> roles;
    @ApiModelProperty(position = 5, example = "[{\"id\" : \"1\", \"brand\" : \"Ford\", \"model\" : \"Mustang\"}]")
    private List<CreateUpdateCarDto> cars;
}
