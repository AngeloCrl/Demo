package com.app.demo.utils;

import com.app.demo.user.dto.RegisterDto;
import com.app.demo.user.dto.UserResponseDto;
import com.app.demo.user.dto.UserRoleDto;
import com.app.demo.user.model.User;
import com.app.demo.user.model.UserRole;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User registerDtoToUser(RegisterDto registerDto) {
        List<UserRoleDto> roles = registerDto.getRoles();
        registerDto.setRoles(null);

        User user = modelMapper.map(registerDto, User.class);

        roles.forEach(roleDto -> user.addRole(modelMapper.map(roleDto, UserRole.class)));
        return user;
    }

    public UserResponseDto userToUserResponseDto(User user) {
        return modelMapper.map(user, UserResponseDto.class);
    }

}
