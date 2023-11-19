package com.app.demo.user.dto.userPassword;

import com.app.demo.user.validators.ValidPassword;
import com.app.demo.user.dto.jwt.TokenDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetDto {

    @ValidPassword
    private String newPassword;

    TokenDto token;
}
