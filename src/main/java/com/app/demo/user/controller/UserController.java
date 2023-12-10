package com.app.demo.user.controller;

import com.app.demo.email.EmailService;
import com.app.demo.user.dto.*;
import com.app.demo.user.dto.jwt.JwtDto;
import com.app.demo.user.dto.jwt.TokenDto;
import com.app.demo.user.dto.userPassword.ChangePasswordDto;
import com.app.demo.user.dto.userPassword.ForgotPasswordEmailDto;
import com.app.demo.user.dto.userPassword.PasswordResetDto;
import com.app.demo.user.model.User;
import com.app.demo.user.model.UserTokenType;
import com.app.demo.user.service.TokenService;
import com.app.demo.user.service.UserService;
import com.app.demo.utils.UserMapper;
import com.app.demo.utils.GenericResponse;
import com.app.demo.utils.exception.CustomException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@Api(tags = "User")
public class UserController {
    private final UserService userService;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final UserMapper userMapper;
    private final ModelMapper modelMapper;
    private final String resetPasswordEmailSubject;
    private final String resetPasswordMailTextMessage;
    private final String changePasswordEmailSubject;
    private final String changePasswordMailTextMessage;

    public UserController(UserService userService,
                          TokenService tokenService,
                          EmailService emailService,
                          ModelMapper modelMapper,
                          UserMapper userMapper,
                          @Value("${spring.mail.reset.subject}") String resetPasswordEmailSubject,
                          @Value("${spring.mail.reset.text-msg}") String resetPasswordMailTextMessage,
                          @Value("${spring.mail.change.subject}") String changePasswordEmailSubject,
                          @Value("${spring.mail.change.text-msg}") String changePasswordMailTextMessage) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
        this.userMapper = userMapper;
        this.resetPasswordEmailSubject = resetPasswordEmailSubject;
        this.resetPasswordMailTextMessage = resetPasswordMailTextMessage;
        this.changePasswordEmailSubject = changePasswordEmailSubject;
        this.changePasswordMailTextMessage = changePasswordMailTextMessage;
    }

    @PostMapping("/signup")
    @ApiOperation(value = "UserController.register")
    public ResponseEntity<GenericResponse> register(@ApiParam("RegisterDto")
                                                    @Valid @RequestBody RegisterDto registerDto) {
        userService.register(userMapper.registerDtoToUser(registerDto), true);
        return new ResponseEntity<>(new GenericResponse("Registered Successfully"), HttpStatus.CREATED);
    }

    @PostMapping(path = "/signin", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "UserController.login")
    public ResponseEntity<JwtDto> login(@ApiParam("UserSignInDto")
                                        @Valid @RequestBody UserSignInDto userSignInDto) {
        String jwt = userService.signIn(userSignInDto);
        return new ResponseEntity<>(new JwtDto(jwt), HttpStatus.OK);
    }

    @PostMapping(path = "/admin/signin", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "UserController.adminLogin")
    public ResponseEntity<JwtDto> adminLogin(@ApiParam("UserSignInDto")
                                             @Valid @RequestBody UserSignInDto userSignInDto) {
        String jwt = userService.adminSignIn(userSignInDto);
        return new ResponseEntity<>(new JwtDto(jwt), HttpStatus.OK);
    }

    @PostMapping(value = "/search", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Search Users ", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<UsersResponseDto> search(@ApiParam("SearchRequestModel")
                                                   @RequestBody SearchRequestModel request,
                                                   @RequestParam("page") Integer page,
                                                   @RequestParam("offset") Integer offset) {
        UsersResponseDto response = userService.search(request, page, offset);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/all")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "UserController.findAllUsers")
    public ResponseEntity<UsersResponseDto> findAllUsers() {
        UsersResponseDto response = userService.getAll();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "UserController.findUserById", response = UserResponseDto.class, authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<UserResponseDto> findUserById(@PathVariable("id") long id) {
        UserResponseDto user = userService.findById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping(value = "/edit")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "UserController.updateUser")
    public ResponseEntity<GenericResponse> updateUser(HttpServletRequest req,
                                                      @ApiParam("UserDto")
                                                      @RequestBody UserDto editDto) {
        User requester = userService.userInfo(req);
        userService.edit(editDto, requester);
        return new ResponseEntity<>(new GenericResponse("updated successfully"), HttpStatus.OK);
    }

    @DeleteMapping(value = "/id/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "UserController.deleteUserById")
    public ResponseEntity<GenericResponse> deleteUserById(HttpServletRequest req, @PathVariable("id") long id) {
        User requester = userService.userInfo(req);
        userService.deleteById(id, requester);
        return new ResponseEntity<>(new GenericResponse("deleted successfully"), HttpStatus.OK);
    }

    @DeleteMapping(value = "/email/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "UserController.deleteUserByEmail", authorizations = {@Authorization(value = "apiKey")})
    public ResponseEntity<GenericResponse> deleteUserByEmail(HttpServletRequest req, @PathVariable("email") String email) {
        User requester = userService.userInfo(req);
        userService.deleteByEmail(email, requester);
        return new ResponseEntity<>(new GenericResponse("deleted successfully"), HttpStatus.OK);
    }

    @GetMapping("/refresh")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "UserController.refresh")
    public ResponseEntity<JwtDto> refresh(HttpServletRequest req) {
        String jwt = userService.refresh(req.getRemoteUser());
        return new ResponseEntity<>(new JwtDto(jwt), HttpStatus.OK);
    }

    @GetMapping(value = "/me")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "UserController.userInfo")
    public ResponseEntity<UserDto> userInfo(HttpServletRequest req) {
        UserDto userDto = modelMapper.map(userService.userInfo(req), UserDto.class);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/verifyEmail")
    @ApiOperation(value = "UserController.verifyEmail")
    public ResponseEntity<GenericResponse> verifyEmail(@ApiParam("TokenDto")
                                                       @Valid @RequestBody TokenDto tokenDto) {
        String tokenErrors = tokenService.validateToken(tokenDto);
        if (tokenErrors != null) {
            return new ResponseEntity<>(new GenericResponse("An error occurred", tokenErrors), HttpStatus.OK);
        }
        userService.verifyUser(tokenDto);
        return new ResponseEntity<>(new GenericResponse("Thank you for verifying your email"), HttpStatus.OK);
    }

    @PostMapping("/changePassword")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "UserController.changeUserPassword")
    public ResponseEntity<GenericResponse> changeUserPassword(HttpServletRequest req,
                                                              @ApiParam("ChangePasswordDto")
                                                              @Valid @RequestBody ChangePasswordDto changePasswordDto) {
        User user = userService.userInfo(req);
        userService.changeUserPassword(user, changePasswordDto);
//        EmailDto email = EmailDto.builder().recipient(user.getEmail()).subject(changePasswordEmailSubject).text(changePasswordMailTextMessage).build();
//        emailService.sendEmail(email);
        return new ResponseEntity<>(new GenericResponse("Password Changed Successfully"), HttpStatus.OK);
    }

    @PostMapping("/forgotPassword")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "UserController.forgotPasswordRequest")
    public ResponseEntity<GenericResponse> forgotPasswordRequest(@ApiParam("EmailAddress")
                                                                 @Valid @RequestBody ForgotPasswordEmailDto dto) {
        User user = userService.findByEmail(dto.getEmail());
        tokenService.createUserToken(user, UserTokenType.RESET_PASSWORD);
        if (user == null) {
            throw new CustomException("The email doesn't exist", HttpStatus.NOT_FOUND);
        }
//        EmailDto emailResponse = EmailDto.builder().recipient(user.getEmail()).subject(changePasswordEmailSubject).text(changePasswordMailTextMessage).build();
//        emailService.sendEmail(emailResponse);
        return new ResponseEntity<>(new GenericResponse("Forgot Password User Request"), HttpStatus.OK);
    }

    @PostMapping("/resetPassword")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "UserController.resetPassword")
    public ResponseEntity<GenericResponse> resetPassword(@ApiParam("PasswordResetDto")
                                                         @Valid @RequestBody PasswordResetDto passwordResetDto) {
        TokenDto tokenDto = passwordResetDto.getToken();
        String tokenErrors = tokenService.validateToken(tokenDto);
        User user = userService.findByToken(tokenDto.getToken()).orElseThrow(() -> {
            throw new CustomException(String.format("User with token %s doesn't exist", tokenDto.getToken()), HttpStatus.BAD_REQUEST);
        });
        if (tokenErrors != null) {
            return new ResponseEntity<>(new GenericResponse("An error occurred", tokenErrors), HttpStatus.BAD_REQUEST);
        }
        userService.resetPassword(user, passwordResetDto.getNewPassword(), tokenDto.getToken());
//        EmailDto email = EmailDto.builder().recipient(user.getEmail()).subject(resetPasswordEmailSubject).text(resetPasswordMailTextMessage).build();
//        emailService.sendEmail(email);
        return new ResponseEntity<>(new GenericResponse("Password Reset Successfully"), HttpStatus.OK);
    }
}
