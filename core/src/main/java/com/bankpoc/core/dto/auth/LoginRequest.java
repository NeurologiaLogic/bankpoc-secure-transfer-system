package com.bankpoc.core.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

//    @NotBlank(message = "Device ID is Required")
//    private String deviceId;
//
//    @NotBlank(message = "Device Name is Required")
//    private String deviceName;
}
