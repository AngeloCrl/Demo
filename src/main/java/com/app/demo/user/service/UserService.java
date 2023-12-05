package com.app.demo.user.service;

import com.app.demo.car.repository.CarRepository;
import com.app.demo.email.EmailDto;
import com.app.demo.email.EmailService;
import com.app.demo.security.JwtTokenProvider;
import com.app.demo.user.dto.*;
import com.app.demo.user.dto.jwt.TokenDto;
import com.app.demo.user.dto.userPassword.ChangePasswordDto;
import com.app.demo.user.model.*;
import com.app.demo.user.repository.TokenRepository;
import com.app.demo.user.repository.UserRepository;
import com.app.demo.user.repository.UserSpecification;
import com.app.demo.utils.AppUtils;
import com.app.demo.utils.UserMapper;
import com.app.demo.utils.exception.CustomException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final String registrationMailSubject;
    private final String registrationMailTextMessage;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final String INVALID_CREDENTIALS = "Invalid email/password combination is wrong or you didn't verify your email";
    private static final String ENTITY_NOT_FOUND = "Entity not found";

    @Autowired
    public UserService(UserRepository userRepository,
                       CarRepository carRepository, ModelMapper modelMapper,
                       UserMapper userMapper,
                       TokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       TokenService tokenService,
                       EmailService emailService,
                       AuthenticationManager authenticationManager,
                       @Value("${spring.mail.register.subject}") String registrationMailSubject,
                       @Value("${spring.mail.register.text-msg}") String registrationMailTextMessage) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
        this.userMapper = userMapper;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.registrationMailSubject = registrationMailSubject;
        this.registrationMailTextMessage = registrationMailTextMessage;
    }

    public User register(User user, boolean shouldCreateToken) {
        logger.info("Registering User");
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new CustomException("Email is already in use", HttpStatus.BAD_REQUEST);
        }

        if (user.getRoles().stream().anyMatch(role -> role.getRole().equals(RoleType.ROLE_ADMIN))) {
            throw new CustomException("Admins cannot sign up", HttpStatus.BAD_REQUEST);
        }

        try {
            user = updatePasswordAndSave(user, user.getPassword());
            if (shouldCreateToken) {
                tokenService.createUserToken(user, UserTokenType.VERIFY_EMAIL);
//            sendRegistrationMail(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Registering User", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        AppUtils.logObject(user, "Registered User");
        return user;
    }


    public String signIn(UserSignInDto userSignInDto) {
        AppUtils.logObject(userSignInDto, "Signing User");
        try {
            User user = userRepository.findByEmail(userSignInDto.getEmail()).orElseThrow(() -> new CustomException(ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND));
            if (user == null || user.getRoles().stream().noneMatch(userRole -> userRole.getRole().equals(RoleType.ROLE_CLIENT))) {
                throw new CustomException(UserService.INVALID_CREDENTIALS, HttpStatus.BAD_REQUEST);
            }
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userSignInDto.getEmail(), userSignInDto.getPassword()));
            return jwtTokenProvider.createToken(userSignInDto.getEmail(), user.getRoles());
        } catch (AuthenticationException e) {
            throw new CustomException(UserService.INVALID_CREDENTIALS, HttpStatus.BAD_REQUEST);
        }
    }

    public String adminSignIn(UserSignInDto userSignInDto) {
        AppUtils.logObject(userSignInDto, "Admin User SignIn");
        try {
            User user = userRepository.findByEmail(userSignInDto.getEmail()).orElseThrow(() -> new CustomException(ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND));
            if (user == null || user.getRoles().stream().noneMatch(userRole -> userRole.getRole().equals(RoleType.ROLE_ADMIN))) {
                throw new CustomException(UserService.INVALID_CREDENTIALS, HttpStatus.BAD_REQUEST);
            }
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userSignInDto.getEmail(), userSignInDto.getPassword()));
            return jwtTokenProvider.createToken(userSignInDto.getEmail(), user.getRoles());
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new CustomException(UserService.INVALID_CREDENTIALS, HttpStatus.BAD_REQUEST);
        }
    }

    public UsersResponseDto search(SearchRequestModel request, int page, int offset) {
        AppUtils.logObject(request, "Searching User(s)");
        UsersResponseDto response = new UsersResponseDto();
        Pageable pageable = PageRequest.of(page, offset);
        try {
            Page<User> users = userRepository.findAll(Specification.where(UserSpecification.filterCriteria(request)), pageable);
            List<UserResponseDto> userDtoList = users.getContent().stream().map(user -> modelMapper.map(user, UserResponseDto.class)).toList();
            response.setUsers(userDtoList);
            response.setTotalElements(users.getTotalElements());
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Searching User(s)", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @Transactional
    public UserResponseDto findById(long id) {
        logger.info("Fetching User By Id");
        try {
            User user = userRepository.findById(id).orElseThrow(() -> {
                throw new CustomException(String.format("User with id %s doesn't exist", id), HttpStatus.BAD_REQUEST);
            });
            user.getCars();
            return userMapper.userToUserResponseDto(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Fetching User By Id", HttpStatus.BAD_REQUEST);
        }
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public UsersResponseDto getAll() {
        logger.info("Fetching all Users");
        List<UserResponseDto> responseDtoList;
        try {
            responseDtoList = userRepository.findAll().stream().map(userMapper::userToUserResponseDto).toList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Fetching all Users", HttpStatus.BAD_REQUEST);
        }
        return new UsersResponseDto(responseDtoList.size(), responseDtoList);
    }

    @Transactional
    public void edit(UserDto userDto, User requester) {
        AppUtils.logObject(userDto, "Editing User");
        boolean canUpdateRoles = requester.getRoles().stream().anyMatch(userRole -> userRole.getRole().equals(RoleType.ROLE_ADMIN));
        if (!canUpdateRoles && (!userDto.getEmail().equals(requester.getEmail()) || !userDto.getId().equals(requester.getId()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user can only update his own profile");
        }
        try {
            Optional<User> userOptional = userRepository.findById(userDto.getId());
            if (userOptional.isPresent()) {
                User userFromDb = userOptional.get();
                editUserDetails(userDto, canUpdateRoles, userFromDb);
                userRepository.save(userFromDb);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Editing User", HttpStatus.BAD_REQUEST);
        }
    }

    public void deleteById(long id, User requester) {
        logger.info("Deleting User By Id");
        boolean canDelete = requester.getRoles().stream().anyMatch(userRole -> userRole.getRole().equals(RoleType.ROLE_ADMIN) || requester.getId().equals(id));
        if (!canDelete) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user can only delete his own profile");
        }
        // We have to manage the owner-car association first, otherwise the user will not be deleted.
        // This is because we have a bidirectional OneToMany relationship between the uer and the car (a foreign key constraint),
        // and we do not have set cascade for car when deleting a user.
        carRepository.findByOwner_Id(id).forEach(car -> {
            car.setOwner(null);
            carRepository.save(car);
        });
        userRepository.deleteById(id);
    }

    public void deleteByEmail(String email, User requester) {
        logger.info("Deleting User By Email");
        boolean canDelete = requester.getRoles().stream().anyMatch(userRole -> userRole.getRole().equals(RoleType.ROLE_ADMIN) || requester.getEmail().equals(email));
        if (!canDelete) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user can only delete his own profile");
        }
        carRepository.findByOwner_Email(email).forEach(car -> {
            car.setOwner(null);
            carRepository.save(car);
        });
        userRepository.deleteByEmail(email);
    }

    public User userInfo(HttpServletRequest req) {
        logger.info("Fetching User Info By Mail and Token");
        try {
            return userRepository.findByEmail(jwtTokenProvider.getEmail(jwtTokenProvider.resolveToken(req)))
                    .orElseThrow(() -> new CustomException(ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Fetching User Info By Mail and Token", HttpStatus.BAD_REQUEST);
        }
    }

    public String refresh(String email) {
        logger.info("Refreshing User Token");
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND));
            return jwtTokenProvider.createToken(email, user.getRoles());
        } catch (CustomException e) {
            throw new CustomException("Exception While Refreshing User Token", HttpStatus.BAD_REQUEST);
        }
    }

    public Optional<User> findByToken(String tokenValue) {
        logger.info("Fetching User By Token");
        try {
            Token token = tokenRepository.findByToken(tokenValue);
            if (token != null) {
                User user = token.getUser();
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Fetching User By Token", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public void verifyUser(TokenDto tokenDto) {
        logger.info("Verifying User Email");
        try {
            User user = findByToken(tokenDto.getToken()).orElseThrow(() -> new CustomException(String.format("User with token %s doesn't exist", tokenDto.getToken()), HttpStatus.BAD_REQUEST));
            Token verifyEmailtoken = tokenRepository.findByToken(tokenDto.getToken());
            user.removeToken(verifyEmailtoken);
            user.setEmailVerified(true);
            userRepository.save(user);
            tokenService.deleteUserTokenByToken(tokenDto.getToken());
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Verifying User Email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void resetPassword(User user, String newPassword, String token) {
        logger.info("Resetting User Password");
        try {
            Token resetPasswordToken = tokenRepository.findByToken(token);
            user.removeToken(resetPasswordToken);
            updatePasswordAndSave(user, newPassword);
            tokenRepository.save(resetPasswordToken);
            tokenService.deleteUserTokenByToken(token);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Resetting User Password", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void changeUserPassword(User user, ChangePasswordDto changePasswordDto) {
        logger.info("Changing User Password");
        if (passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            updatePasswordAndSave(user, changePasswordDto.getNewPassword());
        } else {
            throw new CustomException("Password you provided as old password is wrong", HttpStatus.NOT_FOUND);
        }
    }

    private void sendRegistrationMail(User user) {
        logger.info("Sending Registration Email");
        try {
            EmailDto email = EmailDto.builder().recipient(user.getEmail())
                    .subject(registrationMailSubject)
                    .text(registrationMailTextMessage).build();
            emailService.sendEmail(email);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("Exception While Sending Registration Email", HttpStatus.BAD_REQUEST);
        }
    }

    private void editUserDetails(UserDto userDto, boolean canUpdateRoles, User userFromDb) {
        logger.info("Updating User Details");
        modelMapper.map(userDto, userFromDb);
        updateUserRole(userDto, canUpdateRoles, userFromDb);
    }

    private List<UserRole> updateUserRole(UserDto userDto, boolean canUpdateRoles, User userFromDb) {
        logger.info("Updating User Roles");
        if (canUpdateRoles) {
            userFromDb.removeAllRoles();
        }
        return userDto.getRoles().stream().map(userRoleDto -> modelMapper.map(userRoleDto, UserRole.class)).toList();
    }

    private User updatePasswordAndSave(User user, String newPassword) {
        logger.info("Updating User Password And Save User");
        String pass = passwordEncoder.encode(newPassword);
        user.setPassword(pass);
        return userRepository.save(user);
    }
}
