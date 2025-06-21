// File: AuthService.java
package org.smarthire.AUTH_SERVICE.SERVICE;

import org.smarthire.AUTH_SERVICE.DTO.*;
import org.smarthire.AUTH_SERVICE.MODELS.User;

public interface AuthService {
    UserDTO registerUser(RegisterRequest registerRequest);
    JwtAuthenticationResponse loginUser(LoginRequest loginRequest);
    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
    OtpResponseDTO checkOTPMatch(OTPVerifyRequestDto otpVerifyRequestDto);
    CreatePasswordResponse resetPassword(CreatePasswordRequest createPasswordRequest);


}
