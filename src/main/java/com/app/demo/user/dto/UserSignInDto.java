package com.app.demo.user.dto;

import com.app.demo.user.constraints.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignInDto {

    @ValidPassword
    private String password;

    private String email;
}
