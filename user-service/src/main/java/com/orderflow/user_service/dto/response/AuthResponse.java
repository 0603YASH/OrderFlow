package com.orderflow.user_service.dto.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "token")
public class AuthResponse {
    private String token;
    private String email;
    private String role;
}
