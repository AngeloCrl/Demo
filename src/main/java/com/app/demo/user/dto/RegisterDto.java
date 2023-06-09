package com.app.demo.user.dto;

import com.app.demo.car.dto.CreateUpdateCarDto;
import com.app.demo.user.constraints.ValidPassword;
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
    private String firstName;

    @NotNull
    private String lastName;

    @Email(message = "Email should be valid")
    @NotNull(message = "please specify an email address")
    private String email;

    @ValidPassword(message = "Password should be valid")
    @NotNull(message = "please specify a password")
    private String password;

    private Set<UserRoleDto> roles;

    private List<CreateUpdateCarDto> cars;
}
