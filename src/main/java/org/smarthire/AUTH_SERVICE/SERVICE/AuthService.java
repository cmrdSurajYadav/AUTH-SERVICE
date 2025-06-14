package org.smarthire.AUTH_SERVICE.SERVICE;


import org.smarthire.AUTH_SERVICE.MODELS.Role;
import org.smarthire.AUTH_SERVICE.MODELS.User;
import org.smarthire.AUTH_SERVICE.REPOSITORY.RoleRepository;
import org.smarthire.AUTH_SERVICE.REPOSITORY.UserRepository;
import org.smarthire.AUTH_SERVICE.DTO.LoginRequest;
import org.smarthire.AUTH_SERVICE.DTO.RegisterRequest;
import org.smarthire.AUTH_SERVICE.DTO.JwtAuthenticationResponse;
import org.smarthire.AUTH_SERVICE.SECURITY.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional; // Import Optional

@Service // Marks this class as a Spring Service component
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    // Constructor Injection: Spring will automatically inject these dependencies
    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Registers a new user in the system.
     * @param registerRequest DTO containing user registration details.
     * @return The registered User entity.
     * @throws RuntimeException if email or phone number is already taken.
     */
    public User registerUser(RegisterRequest registerRequest) {
        // Check if email already exists
        if (userRepository.existsByEmailIgnoreCase(registerRequest.getEmail())) {
            throw new RuntimeException("Email '" + registerRequest.getEmail() + "' is already taken!");
        }
        // Check if phone number already exists
        if (userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
            throw new RuntimeException("Phone number '" + registerRequest.getPhoneNumber() + "' is already taken!");
        }

        // Create a new User entity from the DTO
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        // Encode the password before saving to the database for security
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEnable(true); // Enable user account by default upon registration

        // Determine the role for the new user
        // Roles in Spring Security often start with "ROLE_" (e.g., "ROLE_USER", "ROLE_ADMIN")
        String desiredRoleName = "ROLE_" + registerRequest.getRole().toUpperCase(); // Ensure "ROLE_" prefix for consistency

        Role optionalRole = roleRepository.findByRoleName(desiredRoleName);
        Role role;


            role = Role.builder().roleName(desiredRoleName).build();
            roleRepository.save(role); // Save the newly created role

        user.setRole(role); // Assign the single role to the user

        // Save the new user to the database
        return userRepository.save(user);
    }

    /**
     * Authenticates a user and generates a JWT access token.
     * @param loginRequest DTO containing user login credentials (email and password).
     * @return JwtAuthenticationResponse containing the generated JWT token.
     * @throws org.springframework.security.core.AuthenticationException if authentication fails.
     */
    public JwtAuthenticationResponse loginUser(LoginRequest loginRequest) {
        // Authenticate the user using Spring Security's AuthenticationManager
        // This process involves:
        // 1. Loading UserDetails via CustomUserDetailsService using loginRequest.getEmail()
        // 2. Comparing loginRequest.getPassword() with the encoded password from UserDetails
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(), // Principal (username/email)
                        loginRequest.getPassword() // Credentials (raw password)
                )
        );

        // Set the authenticated user in Spring Security's context
        // This is important so that subsequent security checks (e.g., @PreAuthorize) work
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate the JWT token for the authenticated user
        String jwt = jwtTokenProvider.generateToken(authentication);

        // Return the JWT response DTO
        return new JwtAuthenticationResponse(jwt);
    }
}