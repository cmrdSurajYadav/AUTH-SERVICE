package org.smarthire.AUTH_SERVICE.SECURITY;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom implementation of Spring Security's AuthenticationEntryPoint.
 * This class handles unauthorized access attempts to protected resources.
 * When an unauthenticated user tries to access a protected endpoint,
 * Spring Security's filter chain will invoke the commence() method of this class.
 * It sends an HTTP 401 Unauthorized response to the client.
 */
@Component // Marks this class as a Spring component to be managed by the IoC container
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * This method is invoked when an unauthenticated user tries to access a protected resource.
     * It sends an HTTP 401 Unauthorized error response to the client.
     *
     * @param request The HttpServletRequest being processed.
     * @param response The HttpServletResponse to send the error.
     * @param authException The AuthenticationException that caused the commence method to be called.
     * This could be because no valid token was found, or the token was malformed/expired.
     * @throws IOException If an input or output exception occurs.
     * @throws ServletException If a servlet-specific error occurs.
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // Log the exception for debugging purposes (optional, but recommended for production)
        // System.out.println("Unauthorized error. Message - " + authException.getMessage());
        // Or using a logger: log.error("Unauthorized error. Message - {}", authException.getMessage());

        // Send an HTTP 401 Unauthorized response
        // This tells the client that authentication is required or has failed.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}