package com.app.demo.user.dto;

import com.app.demo.user.validators.ValidPassword;
import lombok.*;

//@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignInDto {

    @ValidPassword
    private String password;

    private String email;
}
