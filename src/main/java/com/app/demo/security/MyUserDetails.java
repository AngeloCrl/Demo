package com.app.demo.security;

import com.app.demo.user.model.User;
import com.app.demo.user.repository.UserRepository;
import com.app.demo.utils.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetails implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetails(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // This method is used to load user details (including authentication and authorization information)
    // based on a given username (in this case, email):
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException("User '" + email + "' not found", HttpStatus.NOT_FOUND));

        // User is considered disabled if their email is not verified:
        boolean isDisabled = !user.isEmailVerified();

        // Construct and return a UserDetails Object using the information retrieved from the User entity: (username, password, authorities-roles),
        // and various account status flags (accountExpired, accountLocked, credentialsExpired, and whether the account is disabled).
        return org.springframework.security.core.userdetails.User.withUsername(email).password(user.getPassword())
                .authorities(user.getRoles()) // -> set the Authorities for the User to be the userRole property.
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(isDisabled).build();
    }
}
