package org.smarthire.AUTH_SERVICE.CONFIG;

import org.smarthire.AUTH_SERVICE.SECURITY.JwtAccessDeniedHandler;
import org.smarthire.AUTH_SERVICE.SECURITY.JwtAuthenticationEntryPoint;
import org.smarthire.AUTH_SERVICE.SECURITY.JwtAuthenticationFilter;
import org.smarthire.AUTH_SERVICE.constants.UrlConstants; // Ensure this imports your UrlConstants class
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer; // Import this
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter authenticationFilter;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(JwtAuthenticationEntryPoint authenticationEntryPoint,
                          JwtAuthenticationFilter authenticationFilter,
                          JwtAccessDeniedHandler accessDeniedHandler) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationFilter = authenticationFilter;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Configures WebSecurity to ignore specific request patterns.
     * This completely bypasses the Spring Security filter chain for these paths,
     * which is ideal for public static resources like Swagger UI assets and API docs.
     * This is the recommended approach in modern Spring Security (6+).
     *
     * @return WebSecurityCustomizer instance.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/swagger-ui.html",         // Main Swagger UI HTML page
                "/swagger-ui/**",           // All Swagger UI static resources (JS, CSS, images, etc.)
                "/v3/api-docs/**",          // The OpenAPI 3 API definition endpoint (crucial for SpringDoc)
                "/swagger-resources/**",    // Swagger resources
                "/swagger-resources",       // More Swagger resources
                "/webjars/**",              // Webjars are used to serve Swagger UI assets
                "/configuration/ui",        // UI configuration for Swagger
                "/configuration/security"   // Security configuration for Swagger
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint) // Custom entry point for unauthorized access
                        .accessDeniedHandler(accessDeniedHandler) // Custom handler for access denied
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Use stateless sessions for JWT
                )
                .authorizeHttpRequests(authorize -> authorize
                        // Allow access to all paths defined in UrlConstants.PUBLIC_URL
                        .requestMatchers(UrlConstants.PUBLIC_URL).permitAll()

                        // Your specific public API endpoints, if not covered by PUBLIC_URL
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()

                        // Role-based access
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                );

        // Add your custom JWT authentication filter before Spring Security's default filter
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
