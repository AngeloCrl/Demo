package com.app.demo.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

//@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersResponseDto {
    private long totalElements;
    private List<UserResponseDto> users;
}
