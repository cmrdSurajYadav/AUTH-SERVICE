// src/main/java/org/smarthire/AUTH_SERVICE/SECURITY/JwtTokenProvider.java
package org.smarthire.AUTH_SERVICE.SECURITY;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

// --- IMPORTANT: Add these logging imports ---
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtTokenProvider {

    // --- IMPORTANT: Initialize a logger instance ---
    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    // --- Ensure this matches your application.properties (jwt.expiration, not jwt.expiration.ms) ---
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    public String generateToken(Authentication authentication) {
        String username = authentication.getName(); // This will be the email from CustomUserDetailsService
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        // --- DEBUGGING LOGS: START TOKEN GENERATION ---
        log.info("Attempting to generate JWT for user: {}", username);
        log.debug("Roles in token: {}", roles); // Use debug for less critical info
        log.debug("Current time (ms): {}", now.getTime());
        log.debug("Token expiration duration (ms): {}", jwtExpirationMs);
        log.debug("Calculated token expiry date: {}", expiryDate);
        // Do NOT log the raw jwtSecret or generated token itself here in INFO/DEBUG unless absolutely necessary and securely handled!

        String jwt = null;
        try {
            jwt = Jwts.builder()
                    .setSubject(username) // The user identifier
                    .claim("roles", roles) // Add roles as a custom claim
                    .setIssuedAt(now) // When the token was issued
                    .setExpiration(expiryDate) // When the token expires
                    .signWith(key(), SignatureAlgorithm.HS256) // Sign with the secret key and algorithm
                    .compact(); // Build and serialize the JWT to a compact string

            log.info("JWT generation successful for user: {}. Token length: {}", username, jwt.length());
        } catch (Exception e) {
            // --- DEBUGGING LOGS: CATCH AND LOG ANY EXCEPTION DURING GENERATION ---
            log.error("FATAL: JWT token generation failed for user {}. Error: {}", username, e.getMessage(), e);
            // The 'e' at the end will print the full stack trace, which is vital for diagnosis.
        }
        return jwt; // Will be null if an exception occurred in the try block
    }

    private Key key() {
        byte[] keyBytes = null;
        try {
            keyBytes = Decoders.BASE64.decode(jwtSecret);

            // --- DEBUGGING LOGS: CHECK KEY DECODING ---
            if (keyBytes == null || keyBytes.length == 0) {
                log.error("JWT secret key decoded to null or empty bytes! This means 'jwt.secret' in application.properties/yml is invalid or empty after Base64 decoding.");
                throw new IllegalArgumentException("JWT secret key is invalid or empty after Base64 decoding. Please check your configuration.");
            }
            log.debug("JWT secret key decoded successfully. Length: {} bytes.", keyBytes.length);

        } catch (IllegalArgumentException e) {
            log.error("Base64 decoding of JWT secret failed: {}. Make sure 'jwt.secret' is a valid Base64 string.", e.getMessage(), e);
            throw e; // Re-throw to propagate the configuration error
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // --- Validation methods (getEmailFromToken, validateToken) ---
    // These are from your provided code, still using setSigningKey and parseClaimsJws
    // Consider migrating to verifyWith and parseSignedClaims.getPayload() for latest JJWT API (as discussed)

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SignatureException ex) {
            log.warn("JWT signature does not match locally computed signature for token: {}. Message: {}", token, ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.warn("Invalid JWT token: {}. Message: {}", token, ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.warn("Expired JWT token: {}. Message: {}", token, ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.warn("Unsupported JWT token: {}. Message: {}", token, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.warn("JWT claims string is empty or invalid: {}. Message: {}", token, ex.getMessage());
        } catch (Exception ex) { // Catch any other unexpected exceptions during validation
            log.error("An unexpected error occurred during JWT validation for token: {}. Error: {}", token, ex.getMessage(), ex);
        }
        return false;
    }
}