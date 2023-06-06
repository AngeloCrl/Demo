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
public class SearchModel {

    private String firstName;

    private String lastName;

    private String email;

    private RoleType role;
}
