package com.app.demo.user.service;

import com.app.demo.car.model.Car;
import com.app.demo.email.EmailService;
import com.app.demo.user.model.RoleType;
import com.app.demo.user.model.User;
import com.app.demo.user.model.UserRole;
import com.app.demo.user.repository.TokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserEntityServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private TokenRepository tokenRepository;
    @MockBean
    private EmailService emailService;

    @Test
    void register(){

        // Given
        UserRole testUserRole = new UserRole(RoleType.ROLE_CLIENT);
        Car testCar = Car.builder()
                .brand("TestBrand")
                .model("TestModel")
                .build();

        User testUserEntity = User.builder().firstName("testFirstName").lastName("testLastName")
                .email("testUserEmail@gmail.com").password("Test1231!").build();
        testUserEntity.addRole(testUserRole);
        testUserEntity.addCar(testCar);
        when(emailService.sendEmail(any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        // When
        User actualUserEntity = userService.register(testUserEntity, false);

        // Then
        assertThat(Objects.requireNonNull(actualUserEntity).getEmail(), equalTo("testUserEmail@gmail.com"));
        assertThat(actualUserEntity.getPassword(), notNullValue());
        assertThat(actualUserEntity.getRoles().size(), equalTo(1));
        assertThat(actualUserEntity.getCars().size(), equalTo(1));
        actualUserEntity.getCars().forEach(car -> assertThat(car.getBrand(), equalTo("TestBrand")));
        actualUserEntity.getRoles().forEach(role -> assertThat(role.getRole(), equalTo(RoleType.ROLE_CLIENT)));
    }
}