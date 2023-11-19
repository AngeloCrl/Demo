package com.app.demo.user.dto;

import com.app.demo.user.model.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestModel {
    private String firstName;
    private String lastName;
    private RoleType role;
    private String carBrand;
}
