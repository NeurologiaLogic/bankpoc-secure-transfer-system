package com.bankpoc.core.controller;

import com.bankpoc.core.domain.device.DeviceService;
import com.bankpoc.core.domain.jwt.JwtService;
import com.bankpoc.core.domain.user.User;
import com.bankpoc.core.domain.user.UserService;
import com.bankpoc.core.dto.auth.LoginRequest;
import com.bankpoc.core.dto.auth.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
    final UserService userService;
    final PasswordEncoder passwordEncoder;
    final AuthenticationManager authenticationManager;
    final DeviceService deviceService;
    final JwtService jwtService;

    private final String JWT_COOKIE_NAME = "jwt-token";

    @PostMapping("/register")
    public String createUser(@Valid @RequestBody RegisterRequest userRequest) {
        String hashedPassword = passwordEncoder.encode(userRequest.getPassword());
        User user = userService.register(userRequest.getFullName(),hashedPassword, userRequest.getEmail(), userRequest.getPhoneNumber());
        return "User, Account, and Card created successfully!";
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest userRequest, HttpServletResponse response, HttpServletRequest request) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userRequest.getEmail(),
                        userRequest.getPassword()
                );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        if(authentication.isAuthenticated()) {
            String token = jwtService.generateToken(userRequest.getEmail());
            ResponseCookie cookie = ResponseCookie.from(JWT_COOKIE_NAME, token)
                    .httpOnly(false)       // <-- IMPORTANT: Protects from XSS
                    .secure(false)         // <-- IMPORTANT: Send only over HTTPS (set to false for HTTP in dev)
                    .path("/")            // <-- Makes it available to your whole site
                    .sameSite("Lax")   // <-- Good for CSRF protection
                    .maxAge(60 * 30)
                    .build();
            response.addHeader("Set-Cookie", cookie.toString());
//             Tracing the app
//            String ipAddress = request.getHeader("X-Forwarded-For");
//            if (ipAddress == null || ipAddress.isEmpty()) {
//                ipAddress = request.getRemoteAddr();
//            }
//
//            Optional<User> userOptional = userService.findByEmail(userRequest.getEmail());
//
//            if(userOptional.isPresent()) {
//
//                User user = userOptional.get();
//
//                deviceService.handleLogin(
//                        user,
//                        userRequest.getDeviceId(),
//                        userRequest.getDeviceName(),
//                        ipAddress
//                );
//            } else {
//                throw new UsernameNotFoundException("Authenticated user details are missing.");
//            }

            return ResponseEntity.ok("Login successful");
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    @PostMapping("/otp/request")
    public String otpRequest(@Valid @RequestBody RegisterRequest userRequest) {
        return "User otp successfully!";
    }

    @PostMapping("/otp/verify")
    public String otpVerify(@Valid @RequestBody RegisterRequest userRequest) {
        return "User login successfully!";
    }

}
