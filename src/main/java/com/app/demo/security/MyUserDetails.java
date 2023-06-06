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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException("User '" + email + "' not found", HttpStatus.NOT_FOUND));

        boolean isDisabled = !user.isEmailVerified();

        return org.springframework.security.core.userdetails.User.withUsername(email).password(user.getPassword())
                .authorities(user.getRoles())
                .accountExpired(false).accountLocked(false)
                .credentialsExpired(false).disabled(isDisabled).build();
    }

}
