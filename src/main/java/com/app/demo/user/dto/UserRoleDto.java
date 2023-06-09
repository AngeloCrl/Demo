package com.app.demo.user.dto;

import com.app.demo.user.model.RoleType;
import lombok.*;

//@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@JsonDeserialize(using = UserRoleDeserializer.class)
public class UserRoleDto {
    private RoleType role;
}
