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

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private TokenRepository tokenRepository;
    @MockBean
    private EmailService emailService;

    @Test
    void register() {

        // Given
        String testUserEmail = "testUserEmail@gmail.com";
        String testUserPassword = "Test1231!";
        String testFirstName = "firstName";
        String testLastName = "lastName";
        UserRole userRole = new UserRole(RoleType.ROLE_CLIENT);
        Car testCar = Car.builder()
                .brand("Brand")
                .model("Model")
                .creationDate(LocalDateTime.now())
                .lastModDate(LocalDateTime.now())
                .build();
        User testUser = User.builder().firstName(testFirstName).lastName(testLastName).email(testUserEmail).password(testUserPassword).build();
        testUser.addRole(userRole);
        testUser.addCar(testCar);
        when(emailService.sendEmail(any())).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        // When
        User actualUser = userService.register(testUser, false);

        // Then
        assertThat(actualUser.getEmail(), equalTo(testUserEmail));
        assertThat(actualUser.getPassword(), notNullValue());
        assertThat(actualUser.getRoles().size(), equalTo(1));
        assertThat(actualUser.getCars().size(), equalTo(1));
        actualUser.getCars().forEach(car -> assertThat(car.getBrand(), equalTo("Brand")));
        actualUser.getRoles().forEach(role -> assertThat(role.getRole(), equalTo(RoleType.ROLE_CLIENT)));
    }
}