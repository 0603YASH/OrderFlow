package com.orderflow.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "password")
public class LoginRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
