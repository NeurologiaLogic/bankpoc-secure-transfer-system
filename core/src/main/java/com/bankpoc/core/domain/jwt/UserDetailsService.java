package com.bankpoc.core.domain.jwt;

import com.bankpoc.core.domain.user.User;
import com.bankpoc.core.domain.user.UserRepository;
import com.bankpoc.core.exception.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
@AllArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userInfo = userRepository.findByEmail(email);
        if (userInfo.isEmpty()) {
            throw ApiException.notFound("User not found with email: " + email);
        }
        User user = userInfo.get();
        return new com.bankpoc.core.domain.jwt.UserDetails(user);
    }
}
