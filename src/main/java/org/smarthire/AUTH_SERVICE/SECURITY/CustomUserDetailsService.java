package org.smarthire.AUTH_SERVICE.SECURITY;

import org.smarthire.AUTH_SERVICE.MODELS.User;
import org.smarthire.AUTH_SERVICE.REPOSITORY.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections; // Import Collections

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { // Use email for lookup
        User user = userRepository.findByEmail(email) // Find user by email
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Get the single role name from the user's role object
        String roleName = user.getRole().getRoleName();

        // Spring Security expects roles to be prefixed with "ROLE_"
        // For example, if your roleName is "ADMIN", Spring Security will look for "ROLE_ADMIN"
        // in hasRole("ADMIN"). We add the "ROLE_" prefix here.
        GrantedAuthority authority = new SimpleGrantedAuthority(roleName); // Assuming roleName already has "ROLE_" prefix

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // Principal name (used by Authentication.getName())
                user.getPassword(),
                user.getEnable(), // Account enabled status
                true, // Account non-expired
                true, // Credentials non-expired
                true, // Account non-locked
                Collections.singletonList(authority) // Use Collections.singletonList for a single role
        );
    }
}