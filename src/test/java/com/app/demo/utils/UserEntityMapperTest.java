package com.app.demo.utils;

import com.app.demo.user.dto.RegisterDto;
import com.app.demo.user.dto.UserRoleDto;
import com.app.demo.user.model.RoleType;
import com.app.demo.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@ActiveProfiles("test")
class UserEntityMapperTest {
    @Autowired
    UserMapper userMapper;

    @Test
    void registerDtoToUserTest() {
        //Given
        RegisterDto registerDto = RegisterDto.builder()
                .firstName("firstname")
                .lastName("lastname")
                .email("lalo@gmail.com")
                .password("Lalo1231!")
                .roles(Set.of(UserRoleDto.builder().role(RoleType.ROLE_CLIENT).build()))
                .cars(new ArrayList<>())
                .build();

        //When
        User userEntity = userMapper.registerDtoToUser(registerDto);

        //Then
        assertThat(registerDto.getFirstName(), equalTo(userEntity.getFirstName()));
        assertThat(registerDto.getLastName(), equalTo(userEntity.getLastName()));
        assertThat(registerDto.getEmail(), equalTo(userEntity.getEmail()));
        assertThat(registerDto.getPassword(), equalTo(userEntity.getPassword()));
        assertThat(userEntity.getRoles().size(), equalTo(1));
        userEntity.getRoles().forEach(role -> assertThat(role.getRole(), equalTo(RoleType.ROLE_CLIENT)));
    }
}