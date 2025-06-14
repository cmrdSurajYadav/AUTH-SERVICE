package org.smarthire.AUTH_SERVICE.SECURITY;

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.smarthire.AUTH_SERVICE.DTO.ApiResponse;
import org.smarthire.AUTH_SERVICE.DTO.ErrorResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        if (response.getHeader("ex") == null) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .status(401)
                    .errorMessage("Request has been blocked due to Unauthenticated Source")
                    .subErrors(Arrays.asList("Request has been blocked due to Unauthenticated Source"))
                    .build();

            ApiResponse<Void> apiResponse = new ApiResponse<>(errorResponse);
            apiResponse.setStatus(errorResponse.getStatus());

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(apiResponse);

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(jsonResponse);
        }
    }
}
