package org.smarthire.AUTH_SERVICE.DTO;

import lombok.Data;

@Data
public class LoginRequest {
    private String email; // Use email for login
    private String password;
}