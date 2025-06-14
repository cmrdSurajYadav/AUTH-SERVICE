package org.smarthire.AUTH_SERVICE.SECURITY;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom implementation of Spring Security's AccessDeniedHandler.
 * This class is invoked by the ExceptionTranslationFilter when an authenticated user
 * attempts to access a protected resource but does not have the required authorization (roles).
 * It sends an HTTP 403 Forbidden response to the client.
 */
@Component // Marks this class as a Spring component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Handles an AccessDeniedException.
     * This method is called when an authenticated user is denied access to a resource.
     * It sends an HTTP 403 Forbidden error response to the client.
     *
     * @param request The HttpServletRequest being processed.
     * @param response The HttpServletResponse to send the error.
     * @param accessDeniedException The AccessDeniedException that occurred.
     * @throws IOException If an input or output exception occurs.
     * @throws ServletException If a servlet-specific error occurs.
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // Log the exception for debugging purposes (optional, but recommended for production)
        // System.out.println("Access Denied error. Message - " + accessDeniedException.getMessage());
        // Or using a logger: log.error("Access Denied error. Message - {}", accessDeniedException.getMessage());

        // Send an HTTP 403 Forbidden response
        // This indicates that the server understood the request but refuses to authorize it.
        response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
    }
}