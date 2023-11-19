package com.app.demo.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserIdDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    public UserIdDto(Long id) {
        this.id = id;
    }
}
