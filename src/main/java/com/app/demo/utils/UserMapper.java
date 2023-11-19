package com.app.demo.utils;

import com.app.demo.car.model.Car;
import com.app.demo.user.dto.RegisterDto;
import com.app.demo.user.dto.UserResponseDto;
import com.app.demo.user.model.User;
import com.app.demo.user.model.UserRole;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserMapper {

    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User registerDtoToUser(RegisterDto registerDto) {
        User user = modelMapper.map(registerDto, User.class);
        Set<UserRole> roles = new HashSet<>();
        List<Car> cars = new ArrayList<>();

        if (ObjectUtils.isNotEmpty(user.getRoles())) {
            registerDto.getRoles().forEach(roleDto ->
                    roles.add(modelMapper.map(roleDto, UserRole.class)));
            roles.forEach(userRole -> userRole.setUser(user));
        }

        if (ObjectUtils.isNotEmpty(registerDto.getCars())) {
            cars = registerDto.getCars().stream().map(car -> modelMapper.map(car, Car.class)).toList();
            cars.forEach(car -> car.setOwner(user));
        }
        user.setRoles(roles);
        user.setCars(cars);
        return user;
    }

    public UserResponseDto userToUserResponseDto(User user) {
        return modelMapper.map(user, UserResponseDto.class);
    }
}
