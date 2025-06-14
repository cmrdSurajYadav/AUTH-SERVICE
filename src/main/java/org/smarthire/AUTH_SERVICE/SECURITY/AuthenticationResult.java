package org.smarthire.AUTH_SERVICE.SECURITY;


import org.smarthire.AUTH_SERVICE.MODELS.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationResult {

    private boolean success;
    private String token;
    private User user;
    private String message;
    private long expiresIn; // Token expiration time in seconds

    // Convenience methods
    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }
}