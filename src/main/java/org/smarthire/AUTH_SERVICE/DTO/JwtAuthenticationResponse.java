package org.smarthire.AUTH_SERVICE.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";

    public JwtAuthenticationResponse(String jwt) {
        this.accessToken=jwt;
    }
    // Optionally include refresh token
    // private String refreshToken;
}