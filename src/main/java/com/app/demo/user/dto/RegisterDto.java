package com.app.demo.user.dto;

import com.app.demo.car.dto.CreateUpdateCarDto;
import com.app.demo.user.validators.ValidPassword;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

//@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    @NotNull
    @ApiModelProperty(position = 1, example = "Walter")
    private String firstName;
    @NotNull
    @ApiModelProperty(position = 2, example = "White")
    private String lastName;
    @Email(message = "Email should be valid")
    @NotNull(message = "please specify an email address")
    @ApiModelProperty(position = 3, example = "w.white@gmail.com")
    private String email;
    @ValidPassword(message = "Password should be valid")
    @NotNull(message = "please specify a password")
    @ApiModelProperty(position = 4, example = "Walter1234!")
    private String password;
    @ApiModelProperty(position = 5, example = "[{\"role\" : \"ROLE_CLIENT\"}]")
    private Set<UserRoleDto> roles;
    @ApiModelProperty(position = 6, example = "[{\"brand\" : \"Ford\", \"model\" : \"Mustang\"}]")
    private List<CreateUpdateCarDto> cars;
}
