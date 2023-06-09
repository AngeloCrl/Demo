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
class UserMapperTest {
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
        User user = userMapper.registerDtoToUser(registerDto);

        //Then
        assertThat(registerDto.getFirstName(), equalTo(user.getFirstName()));
        assertThat(registerDto.getLastName(), equalTo(user.getLastName()));
        assertThat(registerDto.getEmail(), equalTo(user.getEmail()));
        assertThat(registerDto.getPassword(), equalTo(user.getPassword()));
        assertThat(user.getRoles().size(), equalTo(1));
        user.getRoles().forEach(role -> assertThat(role.getRole(), equalTo(RoleType.ROLE_CLIENT)));
    }
}