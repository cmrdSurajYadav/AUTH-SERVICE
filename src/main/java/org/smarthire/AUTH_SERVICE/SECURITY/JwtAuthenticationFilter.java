package org.smarthire.AUTH_SERVICE.SECURITY;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils; // Import for StringUtils
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Custom JWT Authentication Filter that extends OncePerRequestFilter
 * to ensure that the filter is executed only once per request.
 * It extracts the JWT token from the request, validates it, and sets
 * the authentication in the SecurityContextHolder.
 */
@Component // Marks this class as a Spring component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    // Constructor Injection: Spring will inject JwtTokenProvider and CustomUserDetailsService
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * This method is called for every incoming request.
     * It performs the JWT token validation and authentication setup.
     *
     * @param request The HttpServletRequest being processed.
     * @param response The HttpServletResponse.
     * @param filterChain The FilterChain to continue processing the request.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an input or output exception occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. Get JWT token from HTTP request's Authorization header
            String token = getJwtFromRequest(request);

            // 2. Validate the token
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                // 3. If token is valid, extract user email from it
                String userEmail = jwtTokenProvider.getEmailFromToken(token);

                // 4. Load user details from the database using the email
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);

                // 5. Create an authentication token for Spring Security
                // This indicates the user is authenticated, has no credentials (password already verified),
                // and has the authorities (roles) loaded from UserDetails.
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, // The principal (UserDetails object)
                        null,        // Credentials (set to null as password is no longer needed after validation)
                        userDetails.getAuthorities() // User's roles/authorities
                );

                // 6. Set additional details (like remote IP address)
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 7. Set the authentication in Spring Security's context
                // This makes the user's authentication information available throughout the application
                // (e.g., for @PreAuthorize annotations, SecurityContextHolder.getContext().getAuthentication())
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception ex) {
            // Log any exceptions during token validation/processing
            // For production, use a proper logger instead of System.out.println
            System.out.println("Could not set user authentication in security context: " + ex.getMessage());
            // It's generally better to let Spring Security's exception handling (JwtAuthenticationEntryPoint)
            // deal with this by not setting the authentication, which leads to 401.
        }

        // 8. Continue the filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header.
     * Assumes the format "Bearer <token>".
     * @param request The HttpServletRequest.
     * @return The JWT token string, or null if not found or not in "Bearer" format.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Check if the Authorization header is present and starts with "Bearer "
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Return the token part after "Bearer "
        }
        return null;
    }
}