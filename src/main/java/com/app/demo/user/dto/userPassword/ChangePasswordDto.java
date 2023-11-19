package com.app.demo.user.dto.userPassword;

import com.app.demo.user.validators.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDto {

    @ValidPassword
    private String oldPassword;

    @ValidPassword
    private String newPassword;
}
