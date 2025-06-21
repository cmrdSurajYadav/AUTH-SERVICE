// src/main/java/org/smarthire/AUTH_SERVICE/CONTROLLER/AuthController.java
package org.smarthire.AUTH_SERVICE.CONTROLLER;

import org.smarthire.AUTH_SERVICE.DTO.*;
import org.smarthire.AUTH_SERVICE.MODELS.User;
import org.smarthire.AUTH_SERVICE.SERVICE.AuthService;
import jakarta.validation.Valid; // Make sure this is imported for @Valid
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException; // Import for specific exception handling
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Handles user registration.
     * @param registerRequest DTO containing user registration details.
     * @return ApiResponse with the registered User object on success, or ErrorResponse on failure.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Call the service to handle registration logic
            UserDTO registeredUser = authService.registerUser(registerRequest);
            // Return success API response with HTTP 200 OK
            return ResponseEntity.ok(new ApiResponse<>(registeredUser));
        } catch (RuntimeException e) {
            // For production, map specific exceptions to more precise HTTP status codes (e.g., HttpStatus.CONFLICT)
            // The GlobalExceptionHandler will ideally catch these, but this shows direct handling
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value()) // Use 400 for general validation/business rule errors
                    .errorMessage(e.getMessage())
                    .build();
            return new ResponseEntity<>(new ApiResponse<>(error), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Handles user login and JWT token generation.
     * @param loginRequest DTO containing user login credentials (email and password).
     * @return ApiResponse with JwtAuthenticationResponse containing the access token on success, or ErrorResponse on failure.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Call the service to handle login and token generation
            JwtAuthenticationResponse jwtResponse = authService.loginUser(loginRequest);
            // Return success API response with HTTP 200 OK
            return ResponseEntity.ok(new ApiResponse<>(jwtResponse));
        } catch (BadCredentialsException e) {
            // Catch specific Spring Security authentication failures for more accurate error messages
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value()) // Use 401 for bad credentials
                    .errorMessage("Invalid email or password.")
                    .build();
            return new ResponseEntity<>(new ApiResponse<>(error), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            // Catch any other unexpected exceptions during login
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value()) // Use 500 for unexpected server errors
                    .errorMessage("An error occurred during login: " + e.getMessage())
                    .build();
            return new ResponseEntity<>(new ApiResponse<>(error), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<?>> forgotPassword(@Valid @RequestBody  ForgotPasswordRequest forgotPasswordRequest){

        try {
            ForgotPasswordResponse forgotPasswordResponse = authService.forgotPassword(forgotPasswordRequest);
            return ResponseEntity.ok(new ApiResponse<>(forgotPasswordResponse));

        } catch (Exception e) {
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value()) // Use 500 for unexpected server errors
                    .errorMessage("An error occurred during login: " + e.getMessage())
                    .build();
            return new ResponseEntity<>(new ApiResponse<>(error), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<?>> verifyOTP(@RequestBody OTPVerifyRequestDto otpVerifyRequestDto){
        try {
            OtpResponseDTO OTPResponseDto = authService.checkOTPMatch(otpVerifyRequestDto);
            return ResponseEntity.ok(new ApiResponse<>(OTPResponseDto));
        }catch (Exception e){
            ErrorResponse error = ErrorResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value()) // Use 500 for unexpected server errors
                    .errorMessage("OTP does not match: " + e.getMessage())
                    .build();
            return new ResponseEntity<>(new ApiResponse<>(error), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(@Valid @RequestBody CreatePasswordRequest createPasswordRequest) {
        try {
            CreatePasswordResponse createPasswordResponse = authService.resetPassword(createPasswordRequest);
            return ResponseEntity.ok(new ApiResponse<>(createPasswordResponse));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}